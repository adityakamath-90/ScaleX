package com.coding.networksdk

import com.awesome.network_api.Network
import com.awesome.network_api.TaskPriority
import kotlinx.coroutines.Deferred
import javax.inject.Inject

class Network @Inject constructor(private val network: Network) {
    suspend fun <T> execute(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: Priority = Priority.MEDIUM,
    ): Deferred<Result<T>> {
        return network.execute(url, method, body, headers, priority.mapToTaskPriority())
    }

    suspend fun <T> executeAsync(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: Priority = Priority.MEDIUM,
        onResult: (Result<T>) -> Unit
    ) {
        network.executeAsync(url, method, body, headers, priority.mapToTaskPriority(), onResult)
    }
}

enum class Priority(val priorityValue: Int) {
    HIGH(3),
    MEDIUM(2),
    LOW(1)
}

fun Priority.mapToTaskPriority(): TaskPriority {
    return when (this) {
        Priority.HIGH -> TaskPriority.HIGH
        Priority.MEDIUM -> TaskPriority.MEDIUM
        Priority.LOW -> TaskPriority.LOW
    }
}