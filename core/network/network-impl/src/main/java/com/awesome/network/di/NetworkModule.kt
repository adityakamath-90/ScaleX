package com.awesome.network.di

import com.awesome.network_api.Network
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
    }

    @Provides
    @Singleton
    fun providePriorityTaskExecutor(okHttpClient: OkHttpClient): PriorityTaskExecutor {
        return PriorityTaskExecutor(
            taskQueue = PriorityBlockingQueue(100) { task1: NetworkTask<*>, task2: NetworkTask<*> ->
                task2.priority.ordinal - task1.priority.ordinal
            },
            taskScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            okHttpClient = okHttpClient
        )
    }

    @Provides
    fun provideNetwork(priorityTaskExecutor: PriorityTaskExecutor): Network {
        return NetworkImpl(priorityTaskExecutor)
    }

    @Provides
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().build()
    }
}