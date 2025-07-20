package com.awesome.home

import com.coding.networksdk.Network
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class FeedRepository @Inject constructor(private val network: Network) {

    private var pageNo: Int = 0

    fun getFeed(): Result<List<FeedItem>> {
        val urlConnection =
            URL("https://jsonplaceholder.typicode.com/photos/?page=${pageNo++}&limit=20").openConnection() as HttpURLConnection
        val response = urlConnection.inputStream.bufferedReader().use { it.readText() }
        val itemType = object : TypeToken<List<FeedItem>>() {}.type
        return Result.success(Gson().fromJson(response, itemType))
    }

    data class FeedItem(
        val id: Int,
        val title: String,
        val thumbnailUrl: String
    )
}