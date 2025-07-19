package com.awesome.network_api

import kotlinx.coroutines.Deferred

interface Network {
    suspend fun <T> execute(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
    ) : Deferred<Result<T>>

    suspend fun <T> executeAsync(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
        onResult: (Result<T>) -> Unit
    )
}


