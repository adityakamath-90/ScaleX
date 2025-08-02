package com.awesome.notes

import FeedScreen
import ScaleXTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                    feedItemList = feedViewModel.stateFlow.collectAsStateWithLifecycle(emptyList()).value,
                    onLoadVisibleItems = { end -> feedViewModel.preFetchFeed(offset = end) },
                )
            }
        }
    }
}

