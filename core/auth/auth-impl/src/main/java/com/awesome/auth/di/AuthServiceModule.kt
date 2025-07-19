package com.awesome.auth.di

import com.awesome.network_api.Network
import dagger.Binds

//@Module
//@InstallIn(SingletonComponent::class)
class AuthServiceModule {

    @Binds
    fun provideAuthService(manager: Network): AuthService {
        return AuthServiceImpl(manager)
    }
}