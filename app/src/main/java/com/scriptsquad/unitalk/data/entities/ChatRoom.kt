package com.scriptsquad.unitalk.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatroom_messages_table")
data class ChatRoom (
    @PrimaryKey val id: String,        // Unique identifier for the message
    val chatroomId: String,            // Identifier of the chatroom
    val senderId: String,              // Identifier of the user who sent the message
    val message: String,                // The content of the message
    val timestamp: Long,                // Timestamp of when the message was sent
    val isRead: Boolean = false         // Flag to indicate if the message has been read
)
