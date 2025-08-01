package com.awesome.home.di

import android.content.Context
import com.awesome.home.repository.FeedRepository
import com.coding.networksdk.Network
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    @Provides
    fun provideFeedRepository(
        network: Network,
        @ApplicationContext context: Context
    ): FeedRepository {
        return FeedRepository(network, context)
    }
}