package com.awesome.home.domain


data class User(
    val id: Int,
    val gender: String,
    val date_of_birth: String,
    val job: String,
    val city: String,
    val zipcode: String,
    val latitude: Double,
    val profile_picture: String,
    val email: String,
    val last_name: String,
    val first_name: String,
    val phone: String,
    val street: String,
    val state: String,
    val country: String,
    val longitude: Double
)