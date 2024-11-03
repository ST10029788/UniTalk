package com.scriptsquad.unitalk.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey val id: String,        // Unique identifier for the note
    val title: String,                 // Title of the note
    val content: String,               // Content of the note
    val createdAt: Long,               // Timestamp of when the note was created
    val updatedAt: Long? = null        // Timestamp of when the note was last updated
)
