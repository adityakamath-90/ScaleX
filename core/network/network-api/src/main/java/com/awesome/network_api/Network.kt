package com.awesome.network_api

import kotlinx.coroutines.CompletableDeferred
import java.lang.reflect.Type

interface Network {
    suspend fun <T> execute(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String>? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
        responseType: Type,
        deferred: CompletableDeferred<Result<T>>
    ): Result<T>
}


