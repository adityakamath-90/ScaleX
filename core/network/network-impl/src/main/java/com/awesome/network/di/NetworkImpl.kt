package com.awesome.network.di

import com.awesome.network_api.Network
import com.awesome.network_api.TaskPriority
import java.lang.reflect.Type
import javax.inject.Inject

internal class NetworkImpl @Inject constructor(
    private val taskExecutor: PriorityTaskExecutor
) : Network {

    override suspend fun <T> execute(
        url: String,
        method: String,
        body: String?,
        headers: Map<String, String>?,
        priority: TaskPriority,
        responseType: Type
    ): Result<T> {
        val task = NetworkTask<T>(
            url,
            method,
            body,
            headers,
            priority,
            responseType,
        )
        return taskExecutor.addTask(task)
    }
}
