package com.awesome.notes

import FeedScreen
import ScaleXTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.awesome.home.presentation.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val feedViewModel by viewModels<FeedViewModel>()
        setContent {
            ScaleXTheme {
                FeedScreen(
                    feedItemList = feedViewModel.items,
                    onLoadVisibleItems = { end -> feedViewModel.preFetchFeed(offset = end) },
                    onRefreshClicked = { item -> feedViewModel.updateFeedItem(item.id) }
                )
            }
        }
    }
}

