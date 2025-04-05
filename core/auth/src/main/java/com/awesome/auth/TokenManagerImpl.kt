package com.awesome.auth

import com.awesome.auth.di.AuthService
import javax.inject.Inject

internal class TokenManagerImpl (@Inject val service : AuthService): TokenManager {
    override fun getToken() {
        TODO("Not yet implemented")
    }
}