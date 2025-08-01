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

    // Keep track of which indices have already been prefetched
    private val prefetchedIndices = mutableSetOf<Int>()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("FeedViewModel", "Coroutine exception: ${exception.localizedMessage}", exception)
    }

    fun getFeedIds() {
        viewModelScope.launch(Dispatchers.IO) {
            val feedTopics = repository.fetchIdentifiers()
            _stateFlow.value = feedTopics.take(1000).map {
                FeedItem(id = it.topicId, detail = null)
            }
        }
    }

    fun preFetchFeed(offset: Int, limit: Int = 10) {
        val rangeToFetch =
            (offset until offset + limit).filterNot { prefetchedIndices.contains(it) }
        if (rangeToFetch.isEmpty()) return
        Log.d("FeedViewModel", "Pre-fetching items: $rangeToFetch")
        viewModelScope.launch(handler) {
            withContext(Dispatchers.IO) {
                supervisorScope {
                    val currentItems = _stateFlow.value

                    val itemsToFetch = rangeToFetch.mapNotNull { index ->
                        currentItems.getOrNull(index)?.takeIf { it.detail == null }?.id
                    }

                    val detailResults = itemsToFetch.map { id ->
                        async {
                            try {
                                repository.feedDetail(id)
                            } catch (e: Exception) {
                                if (e is CancellationException) throw e
                                Log.e("FeedViewModel", "Error for id $id: ${e.message}")
                                null
                            }
                        }
                    }.awaitAll()

                    val updatedList = currentItems.mapIndexed { index, item ->
                        if (index in rangeToFetch) {
                            val detail = detailResults.getOrNull(rangeToFetch.indexOf(index))
                            if (item.detail == null && detail != null) {
                                item.copy(
                                    detail = FeedDetail(detail.title, detail.thumbnailUrl)
                                )
                            } else item
                        } else item
                    }

                    if (updatedList != currentItems) {
                        _stateFlow.value = updatedList
                    }

                    prefetchedIndices.addAll(rangeToFetch)
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
