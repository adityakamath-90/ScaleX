package com.awesome.home

import com.coding.networksdk.Network
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    @Provides
    fun provideFeedRepository(network: Network): FeedRepository {
        return FeedRepository(network)
    }
}
