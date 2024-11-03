package com.scriptsquad.unitalk.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials_table")
data class Material(
    @PrimaryKey val id: String,        // Unique identifier for the material
    val title: String,                 // Title of material
    val description: String,           // Description of material
    val url: String,                   // Link to the material
    val uploadedAt: Long               // Timestamp of when material was uploaded
)
