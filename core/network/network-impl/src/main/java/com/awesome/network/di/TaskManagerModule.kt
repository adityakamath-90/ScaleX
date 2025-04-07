package com.awesome.network.di

import PriorityTaskExecutor
import TaskManagerImpl
import com.awesome.network_api.TaskManager
import dagger.Module
import dagger.Provides

@Module
class TaskManagerModule {

    @Provides
    fun providesTaskManager(priorityTaskExecutor: PriorityTaskExecutor)  : TaskManager{
        return TaskManagerImpl(priorityTaskExecutor)
    }
}