import com.awesome.network.di.TaskPriority
import javax.inject.Inject

class TaskManagerImpl @Inject internal constructor(
    private val taskExecutor: PriorityTaskExecutor
) : TaskManager{

    override suspend fun <T> createTask(
        url: String,
        method: String,
        body: String?,
        headers: Map<String, String>?,
        priority: TaskPriority,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val task = NetworkTask(
            url,
            method,
            body,
            headers,
            priority,
            onSuccess,
            onError
        )
        taskExecutor.addTask(task)
    }
}
