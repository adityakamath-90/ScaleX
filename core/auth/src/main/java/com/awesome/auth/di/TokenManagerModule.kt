package com.awesome.auth.di

import com.awesome.auth.TokenManager
import com.awesome.auth.TokenManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class TokenManagerModule {

    @Binds
    @Singleton
    fun getTokenManger(authService: AuthService) : TokenManager {
        return TokenManagerImpl(authService)
    }
}

