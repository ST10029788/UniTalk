package com.scriptsquad.unitalk.data.entities.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.scriptsquad.unitalk.data.entities.ChatRoom

@Dao
interface ChatRoomDao {
    @Insert
    suspend fun insertMessage(chatRoomModel: ChatRoom)

    @Query("SELECT * FROM chatroom_messages_table ORDER BY timestamp DESC")
    fun getAllMessages(): List<ChatRoom>
}
