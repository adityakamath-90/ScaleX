package com.awesome.home.repository

import com.awesome.home.domain.User

data class UserResponse(
    val success: Boolean,
    val message: String,
    val user: User
)