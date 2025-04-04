package com.awesome.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {

    @Singleton
    @Provides
    fun provideAnalytics() : Analytics {
        return Analytics()
    }
}