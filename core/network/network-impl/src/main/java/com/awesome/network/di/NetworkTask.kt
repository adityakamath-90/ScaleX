import com.awesome.network_api.TaskPriority

data class NetworkTask<T>(
    val url: String,
    val method: String,
    val body: String? = null,
    val headers: Map<String, String>? = null,
    val priority: TaskPriority
)