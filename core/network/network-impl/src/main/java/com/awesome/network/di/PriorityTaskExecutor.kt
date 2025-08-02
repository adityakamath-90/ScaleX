package com.awesome.network.di

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PriorityTaskExecutor @Inject constructor(
    private val taskQueue: PriorityBlockingQueue<NetworkTask<*>>,
    private val okHttpClient: OkHttpClient,
    private val taskScope: CoroutineScope,
    private val maxRetries: Int = 3,
    private val backoffTime: Long = 1000L,
    private val workerCount: Int = Runtime.getRuntime().availableProcessors()
) {
    private val gson = Gson()

    @Volatile
    private var isRunning = true

    init {
        repeat(workerCount) { index ->
            taskScope.launch {
                processTasks()
            }
        }
    }

    suspend fun <T> addTask(task: NetworkTask<T>): Result<T> {
        taskQueue.put(task)
        return task.deferred.await()
    }

    private fun processTasks() {
        while (isRunning) {
            val task = taskQueue.poll(100, TimeUnit.MILLISECONDS) ?: continue
            taskScope.launch {
                val result = try {
                    @Suppress("UNCHECKED_CAST")
                    executeTaskWithRetry(task as NetworkTask<Any>)
                } catch (e: Exception) {
                    Result.failure(e)
                }
                @Suppress("UNCHECKED_CAST")
                (task as NetworkTask<Any>).deferred.complete(result)
            }
        }
    }

    private suspend fun <T> executeTaskWithRetry(task: NetworkTask<T>): Result<T> {
        var lastError: Throwable? = null
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val result = executeRequest(task)
                if (result.isSuccess) return result
            } catch (e: Exception) {
                lastError = e
                delay(backoffTime)
            }
            attempt++
        }
        return Result.failure(lastError ?: Exception("Unknown error"))
    }

    private suspend fun <T> executeRequest(task: NetworkTask<T>): Result<T> =
        withContext(taskScope.coroutineContext) {
            try {
                val builder = Request.Builder().url(task.url)

                task.headers?.forEach { (key, value) ->
                    builder.addHeader(key, value)
                }

                val contentType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = task.body?.toRequestBody(contentType)

                when (task.method.uppercase()) {
                    "POST" -> builder.post(requestBody ?: "".toRequestBody())
                    "PUT" -> builder.put(requestBody ?: "".toRequestBody())
                    "DELETE" -> {
                        if (requestBody != null) builder.delete(requestBody)
                        else builder.delete()
                    }

                    else -> builder.get()
                }

                val response = okHttpClient.newCall(builder.build()).execute()
                val bodyString = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    return@withContext Result.failure<T>(Exception("HTTP ${response.code}"))
                }

                val parsed: T = gson.fromJson(bodyString, task.responseType)
                Result.success(parsed)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun cancelAllTasks() {
        taskQueue.clear()
    }

    fun stopProcessing() {
        isRunning = false
        taskScope.cancel()
    }
}

