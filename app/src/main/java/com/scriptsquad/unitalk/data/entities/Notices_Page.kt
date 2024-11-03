package com.scriptsquad.unitalk.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notices_table")
data class Notice(
    @PrimaryKey val id: String,        // Unique identifier for the notice
    val title: String,                 // Title of the notice
    val description: String,           // Detailed description of the notice
    val timestamp: Long                 // Timestamp of when the notice was created
)
