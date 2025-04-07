package com.awesome.auth.di

import com.awesome.network_api.TaskManager
import dagger.Binds
import dagger.Module

@Module
class AuthServiceModule {

    @Binds
    fun provideAuthService(manager: TaskManager) : AuthService {
        return AuthServiceImpl(manager)
    }
}