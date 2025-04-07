package com.awesome.network.di

import PriorityTaskExecutor
import TaskManager
import TaskManagerImpl
import dagger.Module
import dagger.Provides

@Module
class TaskManagerModule {

    @Provides
    fun providesTaskManager(priorityTaskExecutor: PriorityTaskExecutor)  : TaskManager{
        return TaskManagerImpl(priorityTaskExecutor)
    }
}