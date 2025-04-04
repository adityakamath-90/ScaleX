package com.awesome.notes.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes"
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id :Int,
    val title: String,
    val description: String,
    val date : Long
)
