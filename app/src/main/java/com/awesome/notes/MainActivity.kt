package com.awesome.notes

import FeedScreen
import MyApplicationTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.awesome.home.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val feedViewModel by viewModels<FeedViewModel>()
        setContent {
            MyApplicationTheme {
                FeedScreen(feedItemList = feedViewModel.stateFlow.collectAsStateWithLifecycle().value)
            }
        }

    }
}