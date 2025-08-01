package com.awesome.home.repository

import android.content.Context
import android.util.Log
import com.coding.networksdk.Network
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import javax.inject.Inject


class FeedRepository @Inject constructor(
    private val network: Network,
    @ApplicationContext val context: Context
) {

    val client: OkHttpClient = OkHttpClient.Builder().build()

    suspend fun fetchIdentifiers(): List<Topic> {
        val inputStream =
            context.assets.open("topicIds.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(inputStream, object : TypeToken<List<Topic>>() {}.type)
    }

    suspend fun feedDetail(userid: Int?): FeedDetail {
        Log.d("FeedRepository", "feedDetail: $userid")
        val request = Request.Builder()
            .url("https://api.slingacademy.com/v1/sample-data/users/${userid}")
            .build()
        val response = client.newCall(request).execute()

        val body = response.body?.string()
            ?: throw IOException("Empty response body")

        val userResponse = Gson().fromJson(body, UserResponse::class.java)
        return FeedDetail(userid, userResponse.user.first_name, userResponse.user.profile_picture)
    }

    data class FeedDetail(
        val id: Int?,
        val title: String?,
        val thumbnailUrl: String?
    )

    data class Topic(val topicId: Int)
}