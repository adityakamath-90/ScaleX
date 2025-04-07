package com.awesome.network_api

import kotlinx.coroutines.Deferred

interface TaskManager {
    suspend fun <T> createTask(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
    ) : Deferred<Result<T>>
}