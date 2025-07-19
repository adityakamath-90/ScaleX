package com.awesome.home

import com.coding.networksdk.Network
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class FeedRepository @Inject constructor(private val network: Network) {

    fun getRecentFeed(): Result<FeedResponse> {
        val urlConnection =
            URL("https://pokeapi.co/api/v2/pokemon?limit=50").openConnection() as HttpURLConnection
        val response = urlConnection.inputStream.bufferedReader().use { it.readText() }
        return Result.success(Gson().fromJson(response, FeedResponse::class.java))
    }

    data class FeedResponse(
        val count: Int,
        val next: String? = null,
        val previous: String? = null,
        val results: List<FeedItem>
    )

    data class FeedItem(
        val name: String,
        val url: String
    )
}