package com.awesome.network.di

import com.awesome.network_api.Network
import com.awesome.network_api.TaskPriority
import kotlinx.coroutines.Deferred
import javax.inject.Inject

internal class NetworkImpl @Inject constructor(
    private val taskExecutor: PriorityTaskExecutor
) : Network {

    override suspend fun <T> execute(
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
            priority,
        )
        return taskExecutor.addTask(task)
    }

    override suspend fun <T> executeAsync(
        url: String,
        method: String,
        body: String?,
        headers: Map<String, String>?,
        priority: TaskPriority,
        onResult: (Result<T>) -> Unit
    ) {
        val task = NetworkTask<T>(
            url,
            method,
            body,
            headers,
            priority
        )
        taskExecutor.addTask(task)
    }
}
