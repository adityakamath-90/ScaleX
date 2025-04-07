import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PriorityTaskExecutor @Inject constructor(
    private val taskQueue: PriorityBlockingQueue<NetworkTask<*>>,
    private val maxRetries: Int = 3,
    private val backoffTime: Long = 1000L,
    private val taskScope: CoroutineScope
) {
    private val gson = Gson()  // Inject Gson
    private val mutex = Mutex()  // Mutex to protect shared state
    private var isRunning = true

    // Modify to return Deferred<Result<T>>
    suspend fun <T> addTask(task: NetworkTask<T>): Deferred<Result<T>> {
        val deferred = CompletableDeferred<Result<T>>()

        mutex.withLock {
            taskQueue.put(task)  // Thread-safe access to taskQueue
        }

        taskScope.launch {
            executeTaskWithRetry(task, deferred)
        }

        return deferred
    }

    @VisibleForTesting
    private suspend fun processTasks() {
        while (isRunning) {
            val task = mutex.withLock {
                taskQueue.poll(100, TimeUnit.MILLISECONDS)
            }
            if (task != null) {
                taskScope.launch {
                    executeTaskWithRetry(task, CompletableDeferred())
                }
            }
        }
    }

    private suspend fun <T> executeTaskWithRetry(task: NetworkTask<T>, deferred: CompletableDeferred<Result<T>>) {
        var lastError: Throwable? = null
        var attempts = 0
        while (attempts < maxRetries) {
            try {
                val result = executeRequest(task)
                if (result.isSuccess) {
                    deferred.complete(Result.success(result.getOrThrow()))  // Complete with success
                    return
                }
            } catch (e: Exception) {
                lastError = e
                attempts++
                if (attempts < maxRetries) {
                    delay(backoffTime)
                }
            }
        }
        deferred.complete(Result.failure(lastError ?: Exception("Unknown error")))  // Complete with failure
    }

    private suspend fun <T> executeRequest(task: NetworkTask<T>): Result<T> = withContext(taskScope.coroutineContext) {
        val connection = URL(task.url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = task.method
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            task.headers?.forEach { (key, value) -> connection.setRequestProperty(key, value) }

            task.body?.let {
                if (task.method == "POST" || task.method == "PUT") {
                    connection.doOutput = true
                    connection.outputStream.write(it.toByteArray())
                }
            }

            connection.connect()
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<T>() {}.type
            val result: T = deserializeResponse(response, type)

            return@withContext Result.success(result)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        } finally {
            connection.disconnect()
        }
    }

    private fun <T> deserializeResponse(response: String, type: Type): T {
        return gson.fromJson(response, type)
    }

    // Cancels all tasks in the queue
    suspend fun cancelAllTasks() {
        mutex.withLock {
            taskQueue.clear()  // Clear any remaining tasks in the queue
        }
    }

    fun stopProcessing() {
        // Ensure we safely modify isRunning state using a mutex
        taskScope.launch {
            mutex.withLock {
                isRunning = false
            }
        }
        taskScope.cancel()  // Cancel the entire task scope and all related coroutines
    }
}
