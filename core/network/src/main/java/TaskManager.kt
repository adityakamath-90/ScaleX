import com.awesome.network.di.TaskPriority

interface TaskManager {
    suspend fun <T> createTask(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    )
}
