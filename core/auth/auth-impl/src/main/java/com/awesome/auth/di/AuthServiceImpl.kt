package com.awesome.auth.di

import com.awesome.auth_api.Token
import com.awesome.network_api.Network
import kotlinx.coroutines.Deferred
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(val manager: Network) : AuthService {
    override suspend fun getToken(): Result<Token> {
        val deferredResult: Deferred<Result<Token>> = manager.execute(url = "", method = "")
        return deferredResult.await()
    }
}