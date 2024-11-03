package com.scriptsquad.unitalk.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectures_table")
data class Lecture(
    @PrimaryKey val id: String,        // Unique identifier for the lecture
    val title: String,                 // Title of the lecture
    val speaker: String,               // Speaker's name
    val date: Long,                    // Timestamp of when the lecture is scheduled
    val description: String? = null,   // Optional description of the lecture
    val url: String                    // Link to the lecture
)
