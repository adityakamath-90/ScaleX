package com.awesome.notes.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes"
)
data class Note(
    @PrimaryKey
    var id :Int,
    val title: String,
    val description: String,
    val date : Long
)
