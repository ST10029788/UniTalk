package com.scriptsquad.unitalk.Socials_Page.model


data class modelPictures(
    val id: String,
    val imageUrl: String,
    val timestamp: Long,
    val title: String,
    val uid: String
) {
    constructor() : this("", "", 0, "", "") // Initialize with empty values
}

data class modelVideo(

    var id: String = "",
    var timestamp: Long = 0,
    var title: String = "",
    var uid: String = "",
    var videoUrl: String = ""
) {
    constructor() : this("", 0, "", "", "") // Initialize with empty values
}

