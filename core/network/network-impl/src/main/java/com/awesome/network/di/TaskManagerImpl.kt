import com.awesome.network_api.TaskManager
import com.awesome.network_api.TaskPriority
import kotlinx.coroutines.Deferred
import javax.inject.Inject

class TaskManagerImpl @Inject internal constructor(
    private val taskExecutor: PriorityTaskExecutor
) : TaskManager{

    override suspend fun <T> createTask(
        url: String,
        method: String,
        body: String?,
        headers: Map<String, String>?,
        priority: TaskPriority
    )  : Deferred<Result<T>>{
        val task = NetworkTask<T>(
            url,
            method,
            body,
            headers,
            priority
        )
        return taskExecutor.addTask(task)
    }
}
