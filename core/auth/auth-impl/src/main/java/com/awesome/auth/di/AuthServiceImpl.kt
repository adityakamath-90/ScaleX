package com.awesome.auth.di

import com.awesome.auth_api.Token
import com.awesome.network_api.TaskManager
import kotlinx.coroutines.Deferred
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(val manager: TaskManager): AuthService {
    override suspend fun getToken(): Result<Token> {
        val deferredResult:  Deferred<Result<Token>> =  manager.createTask(url = "", method = "")
        return deferredResult.await()
    }
}