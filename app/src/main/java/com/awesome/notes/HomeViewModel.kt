package com.awesome.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.notes.database.Note
import com.awesome.notes.di.NotesReposistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val reposistory: NotesReposistory) : ViewModel() {

     val notesList  = MutableStateFlow(HomeUiState(true, null))

     suspend fun getNotes()  : Flow<HomeUiState>{
        viewModelScope.launch {
            val noteList : Flow<List<Note>> = async { reposistory.getNotes() }.await()
            notesList.emit(HomeUiState(false, noteList.single()))
        }
         return notesList
    }

    data class HomeUiState(val loading: Boolean, val noteList : List<Note>?)

}