package com.awesome.home.presentation

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.home.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<List<FeedItem>>(emptyList())
    val stateFlow = _stateFlow
        .onStart { getFeedIds() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Load 1000 feed item IDs from local assets (e.g., topicIds.json)
    fun getFeedIds() {
        viewModelScope.launch(Dispatchers.IO) {
            val feedTopics = repository.fetchIdentifiers()
            _stateFlow.value = feedTopics.take(1000).map {
                FeedItem(id = it.topicId, detail = null)
            }

            // Optionally prefetch the first page
            preFetchFeed(offset = 0, limit = 10)
        }
    }

    // Prefetch details for a slice (e.g., 10 items at a time)
    fun preFetchFeed(offset: Int, limit: Int = 10) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("FeedViewModel", "Coroutine exception: ${exception.localizedMessage}", exception)
        }

        viewModelScope.launch(handler) {
            withContext(Dispatchers.IO) {
                supervisorScope {
                    val feedItems = _stateFlow.value.drop(offset).take(limit)

                    val detailAsyncList = feedItems.map { item ->
                        async {
                            try {
                                repository.feedDetail(item.id)
                            } catch (e: Exception) {
                                if (e is CancellationException) throw e
                                Log.e("FeedViewModel", "Error for id ${item.id}: ${e.message}")
                                null
                            }
                        }
                    }

                    val details = detailAsyncList.awaitAll()

                    val currentItems = _stateFlow.value.toMutableList()

                    details.forEachIndexed { index, detail ->
                        val targetIndex = offset + index
                        if (targetIndex < currentItems.size && detail != null) {
                            currentItems[targetIndex] = FeedItem(
                                id = detail.id!!,
                                detail = FeedDetail(detail.title, detail.thumbnailUrl)
                            )
                        }
                    }

                    _stateFlow.value = currentItems
                    Log.d("FeedViewModel", "Updated items from $offset to ${offset + limit}")
                }
            }
        }
    }

    @Immutable
    data class FeedItem(
        val id: Int,
        val detail: FeedDetail?
    )

    @Immutable
    data class FeedDetail(
        val name: String?,
        val url: String?
    )
}
