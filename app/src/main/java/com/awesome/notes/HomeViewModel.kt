package com.awesome.notes

import TaskManagerImpl
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel(){

    @Inject
    lateinit var manager: TaskManagerImpl

    suspend fun launch(){
    }
}
