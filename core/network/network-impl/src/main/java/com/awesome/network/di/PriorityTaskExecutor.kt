package com.awesome.network.di

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PriorityTaskExecutor @Inject constructor(
    private val taskQueue: PriorityBlockingQueue<NetworkTask<*>>,
    private val maxRetries: Int = 3,
    private val backoffTime: Long = 1000L,
    private val taskScope: CoroutineScope,
    private val okHttpClient: OkHttpClient
) {
    private val gson = Gson()
    private val mutex = Mutex()
    private var isRunning = true

    // Modify to return Deferred<Result<T>>
    internal suspend fun <T> addTask(task: NetworkTask<T>): Result<T> {
        mutex.withLock {
            taskQueue.put(task)
            return executeRequest()
        }
    }

    @VisibleForTesting
    private suspend fun processTasks() {
        while (isRunning) {
            val task = mutex.withLock {
                taskQueue.poll(100, TimeUnit.MILLISECONDS)
            }
            if (task != null) {
                taskScope.launch {
                    //executeTaskWithRetry(task, CompletableDeferred())
                }
            }
        }
    }

    // Existing retry executor, adapted to return Result<T> (not Deferred)
//    private suspend fun <T> executeTaskWithRetry(task: NetworkTask<T>): Result<T> {
//        var lastError: Throwable? = null
//        var attempts = 0
//        while (attempts < maxRetries) {
//            try {
//                val result = executeRequest()
//                if (result.isSuccess) {
//                    return Result.success(result.getOrThrow())
//                }
//            } catch (e: Exception) {
//                lastError = e
//                attempts++
//                if (attempts < maxRetries) {
//                    delay(backoffTime)
//                }
//            }
//        }
//        return Result.failure(lastError ?: Exception("Unknown error"))
//    }

    private suspend fun <T> executeRequest(): Result<T> = withContext(taskScope.coroutineContext) {
        val task = taskQueue.poll()
            ?: return@withContext Result.failure<T>(IllegalStateException("No task available"))

        try {
            val builder = Request.Builder()
                .url(task.url)

            // Add headers if any
            task.headers?.forEach { (key, value) ->
                builder.addHeader(key, value)
            }

            // Prepare request body if present
            val contentType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody: RequestBody? = task.body?.toRequestBody(contentType)

            // Set HTTP method
            when (task.method.uppercase()) {
                "POST" -> builder.post(requestBody ?: "".toRequestBody())
                "PUT" -> builder.put(requestBody ?: "".toRequestBody())
                "DELETE" -> {
                    if (requestBody != null) {
                        builder.delete(requestBody)
                    } else {
                        builder.delete()
                    }
                }

                else -> builder.get()
            }

            val request = builder.build()

            // Execute the HTTP request
            val response = okHttpClient.newCall(request).execute()

            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure<T>(Exception("HTTP error code: ${response.code}"))
            }

            val result: T = deserializeResponse(responseBody, task.responseType)

            Result.success(result)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun <T> deserializeResponse(response: String, type: Type): T {
        return gson.fromJson(response, type)
    }

    suspend fun cancelAllTasks() {
        mutex.withLock {
            taskQueue.clear()
        }
    }

    fun stopProcessing() {
        taskScope.launch {
            mutex.withLock {
                isRunning = false
            }
        }
        taskScope.cancel()
    }
}
