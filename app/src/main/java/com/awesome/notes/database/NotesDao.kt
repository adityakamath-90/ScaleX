package com.awesome.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Dao
interface NotesDao {

    @Upsert
    suspend fun insertNote(note : Note)

    @Delete
    suspend fun insertNote(id : Int)

    @Query("SELECT * FROM notes")
    suspend fun getNotes() : Flow<List<Note>>
}