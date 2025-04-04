package com.awesome.notes.di

import com.awesome.notes.database.NotesDao
import com.awesome.notes.database.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
internal object DaosModule {

    @Provides
    fun providesNotesDao(database: NotesDatabase) : NotesDao {
        return database.noteDao()
    }
}