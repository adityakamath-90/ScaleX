package com.awesome.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(private val repository: FeedRepository) : ViewModel() {

    private val _stateFlow = MutableStateFlow<MutableList<FeedRepository.FeedItem>>(mutableListOf())
    val stateFlow = _stateFlow.onStart {
        getRecentFeed()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun getRecentFeed() {
        withContext(Dispatchers.IO) {
            val feedListResult = repository.getRecentFeed()
            Log.i("TAG", "getRecentFeed: $feedListResult")
            if (feedListResult.isSuccess) {
                val pokemonList = feedListResult.getOrNull()
                Log.i("TAG", "getRecentFeed: $pokemonList")
                if (pokemonList != null) {
                    _stateFlow.value = pokemonList.toMutableList()
                }
            }
        }
    }
}