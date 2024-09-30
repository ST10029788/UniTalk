package com.scriptsquad.unitalk.Notes.model

//data class for a pdf book model
data class ModelBookPdf(
    val uid: String,            // Unique identifier for the book
    val id: String,             // Identifier for the book
    val title: String,          // Title of the book
    val description: String,    // Description of the book
    val categoryId: String,     // Category identifier
    val url: String,            // URL for accessing the book PDF
    val imageUrl: String,       // URL for the book cover image
    val timestamp: Long,        // Timestamp when the book was added
    val viewCount: Long,        // Number of views for the book
    val downloadsCount: Long     // Number of downloads for the book
) {
    constructor() : this("", "", "", "", "", "", "", 0, 0, 0) // Initialize with empty values
}

