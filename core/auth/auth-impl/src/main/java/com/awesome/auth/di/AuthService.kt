package com.awesome.auth.di

import com.awesome.auth_api.Token

interface AuthService {
    suspend fun getToken() : Result<Token>
}