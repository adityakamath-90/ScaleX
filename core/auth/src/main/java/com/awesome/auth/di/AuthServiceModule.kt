package com.awesome.auth.di

import dagger.Binds
import dagger.Module

@Module
class AuthServiceModule {

    @Binds
    fun provideAuthService() : AuthService {
        return AuthServiceImpl()
    }
}