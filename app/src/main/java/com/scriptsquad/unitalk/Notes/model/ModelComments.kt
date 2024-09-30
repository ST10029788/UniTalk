package com.scriptsquad.unitalk.Notes.model
//Data class representing a comment on a book.
class ModelComments {

    var id: String = ""             // Unique identifier for the comment
    var bookId: String = ""         // Identifier for the associated book
    var timestamp: String = ""       // Timestamp when the comment was made
    var comment: String = ""         // Text of the comment
    var uid: String = ""             // Unique identifier for the user who commented

    constructor()
    constructor(id: String, bookId: String, timestamp: String, comment: String, uid: String) {
        this.id = id
        this.bookId = bookId
        this.timestamp = timestamp
        this.comment = comment
        this.uid = uid
    }


}