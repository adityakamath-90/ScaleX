package com.awesome.auth.di

import com.awesome.auth.TokenManagerImpl
import com.awesome.auth_api.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class TokenManagerModule {

    @Provides
    @Singleton
    fun getTokenManger(authService: AuthService) : TokenManager {
        return TokenManagerImpl(authService)
    }
}

