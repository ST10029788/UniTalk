package com.scriptsquad.unitalk.Notes.model


data class ModelBookPdf(
    val uid: String,
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val url: String,
    val imageUrl: String,
    val timestamp: Long,
    val viewCount: Long,
    val downloadsCount: Long
) {
    constructor() : this("", "", "", "", "", "", "", 0, 0, 0) // Initialize with empty values
}

