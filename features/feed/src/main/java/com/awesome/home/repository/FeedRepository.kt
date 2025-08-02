package com.awesome.home.repository

import android.content.Context
import com.coding.networksdk.Network
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.IOException
import javax.inject.Inject


class FeedRepository @Inject constructor(
    private val network: Network,
    @ApplicationContext val context: Context
) {

    suspend fun fetchIdentifiers(): List<Topic> {
        val inputStream =
            context.assets.open("topicIds.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(inputStream, object : TypeToken<List<Topic>>() {}.type)
    }

    suspend fun feedDetail(userid: Int?): FeedDetail {
        val response = network.execute<UserResponse>(
            "https://api.slingacademy.com/v1/sample-data/users/${userid}", "GET",
            responseType = UserResponse::class.java,
        )
        if (!response.isSuccess) {
            throw IOException("Unexpected code $response")
        }

        val userResponse = response.getOrNull()
            ?: throw IOException("Empty response body")
        return FeedDetail(userid, userResponse.user.first_name, userResponse.user.profile_picture)
    }

    data class FeedDetail(
        val id: Int?,
        val title: String?,
        val thumbnailUrl: String?
    )

    data class Topic(val topicId: Int)
}