package com.awesome.network.di

import com.awesome.network_api.TaskPriority
import kotlinx.coroutines.CompletableDeferred
import java.lang.reflect.Type

data class NetworkTask<T>(
    val url: String,
    val method: String,
    val body: String? = null,
    val headers: Map<String, String>? = null,
    val priority: TaskPriority,
    val responseType: Type,
    val deferred: CompletableDeferred<Result<T>>
) : Comparable<NetworkTask<*>> {
    override fun compareTo(other: NetworkTask<*>): Int {
        return other.priority.ordinal - this.priority.ordinal
    }
}
