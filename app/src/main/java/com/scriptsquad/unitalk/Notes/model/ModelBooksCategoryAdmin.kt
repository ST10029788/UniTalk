package com.scriptsquad.unitalk.Notes.model
// Data class representing a category for books in the admin panel
class ModelBooksCategoryAdmin {

    var id: String = ""            // Unique identifier for the category
    var category: String = ""      // Name or title of the category
    var timestamp: Long = 0        // Timestamp when the category was created
    var uid: String = ""           // Unique identifier for the creator of the category
    constructor()

    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }


}