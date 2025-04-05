package com.awesome.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    suspend fun insertNote(note : Note)

    @Delete
    suspend fun insertNote(id : Int)

    @Query("SELECT * FROM notes")
    suspend fun getNotes() : Flow<List<Note>>
}