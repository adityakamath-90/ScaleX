package com.awesome.home.presentation

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.home.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {

    // Use mutableStateListOf for fine-grained updates and better Compose performance
    private val _items = mutableStateListOf<FeedItem>()
    val items: List<FeedItem> get() = _items

    // Track prefetched indices to avoid redundant fetches
    private val prefetchedIndices = mutableSetOf<Int>()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("FeedViewModel", "Coroutine exception: ${exception.localizedMessage}", exception)
    }

    init {
        // Load initial minimal feed IDs on ViewModel init
        getFeedIds()
    }

    private fun getFeedIds() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val feedTopics = repository.fetchIdentifiers()
                withContext(Dispatchers.Main) {
                    _items.clear()
                    _items.addAll(feedTopics.map {
                        FeedItem(id = it.topicId, detail = null)
                    })
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Failed to fetch feed IDs: ${e.localizedMessage}", e)
            }
        }
    }

    fun preFetchFeed(offset: Int, limit: Int = 10) {
        val rangeToFetch =
            (offset until (offset + limit)).filterNot { prefetchedIndices.contains(it) }
        if (rangeToFetch.isEmpty()) return

        Log.d("FeedViewModel", "Pre-fetching items: $rangeToFetch")

        viewModelScope.launch(handler + Dispatchers.IO) {
            supervisorScope {
                val itemsToFetch = rangeToFetch.mapNotNull { index ->
                    _items.getOrNull(index)?.takeIf { it.detail == null }?.id
                }

                val detailResults = itemsToFetch.map { id ->
                    async {
                        try {
                            repository.feedDetail(id)
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            Log.e("FeedViewModel", "Error fetching detail for id $id: ${e.message}")
                            null
                        }
                    }
                }.awaitAll()

                withContext(Dispatchers.Main) {
                    rangeToFetch.forEachIndexed { i, index ->
                        val detail = detailResults.getOrNull(i)
                        if (detail != null) {
                            val oldItem = _items.getOrNull(index)
                            if (oldItem != null && oldItem.detail == null) {
                                _items[index] = oldItem.copy(
                                    detail = FeedDetail(detail.title, detail.thumbnailUrl)
                                )
                            }
                        }
                    }
                    prefetchedIndices.addAll(rangeToFetch)
                }
            }
        }
    }

    fun updateFeedItem(topicId: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            try {
                val updatedDetail = repository.feedDetail(topicId)
                withContext(Dispatchers.Main) {
                    val index = _items.indexOfFirst { it.id == topicId }
                    if (index >= 0) {
                        val oldItem = _items[index]
                        _items[index] = oldItem.copy(
                            detail = FeedDetail(
                                name = "${updatedDetail.title} : Refreshed",
                                url = updatedDetail.thumbnailUrl
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("FeedViewModel", "Error updating item $topicId: ${e.message}")
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