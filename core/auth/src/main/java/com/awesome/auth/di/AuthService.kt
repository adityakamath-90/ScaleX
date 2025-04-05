package com.awesome.auth.di

interface AuthService {

    data class Token(val accessToken: String, val refreshToken: String, val expiryAt: String)

    fun getToken() : Token
}

class AuthServiceImpl : AuthService {
    override fun getToken(): AuthService.Token {
        return AuthService.Token("","","")
    }
}