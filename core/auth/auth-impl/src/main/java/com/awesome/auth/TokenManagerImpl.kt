package com.awesome.auth

import com.awesome.auth.di.AuthService
import com.awesome.auth_api.Token
import com.awesome.auth_api.TokenManager
import javax.inject.Inject

internal class TokenManagerImpl (@Inject val service : AuthService): TokenManager {
    override suspend fun getToken() : Result<Token> {
        return service.getToken()
    }
}