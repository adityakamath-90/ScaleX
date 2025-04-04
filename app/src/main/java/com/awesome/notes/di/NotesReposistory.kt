package com.awesome.notes.di

import com.awesome.notes.database.Note
import com.awesome.notes.database.NotesDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesReposistory @Inject  constructor(private val notesDao: NotesDao) {

    suspend fun getNotes() : Flow<List<Note>> {
        return notesDao.getNotes()
    }

}