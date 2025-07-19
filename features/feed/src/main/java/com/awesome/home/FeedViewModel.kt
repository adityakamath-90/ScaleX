package com.awesome.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(private val repository: FeedRepository) : ViewModel() {

    val stateFlow = MutableStateFlow<MutableList<FeedRepository.FeedItem>>(mutableListOf())

    init {
        viewModelScope.launch {
            getRecentFeed()
        }
    }

    suspend fun getRecentFeed() {
        withContext(Dispatchers.IO) {
            val pokemonRes = repository.getRecentFeed()
            Log.i("TAG", "getRecentFeed: $pokemonRes")
            if (pokemonRes.isSuccess) {
                val pokemonList = pokemonRes.getOrNull()
                Log.i("TAG", "getRecentFeed: $pokemonList")
                if (pokemonList != null) {
                    stateFlow.value = pokemonList.results.toMutableList()
                }
            }
        }
    }

}