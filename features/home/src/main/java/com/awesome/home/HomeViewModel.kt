package com.awesome.home

import androidx.lifecycle.ViewModel
import com.awesome.auth_api.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(manager: TokenManager) : ViewModel(){

    suspend fun launch(){
    }
}