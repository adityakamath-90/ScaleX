package com.awesome.auth_api

interface TokenManager {
    suspend fun getToken() : Result<Token>
}

data class Token(
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
)