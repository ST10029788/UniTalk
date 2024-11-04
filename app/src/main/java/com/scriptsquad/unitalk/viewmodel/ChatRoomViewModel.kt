package com.scriptsquad.unitalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scriptsquad.unitalk.data.entities.ChatRoom
import com.scriptsquad.unitalk.data.entities.dao.ChatRoomDao
import kotlinx.coroutines.launch

class ChatRoomViewModel(private val chatRoomDao: ChatRoomDao) : ViewModel() {
    fun insertMessage(message: ChatRoom) {
        viewModelScope.launch {
            chatRoomDao.insertMessage(message)
        }
    }

    fun getAllMessages(): List<ChatRoom> {
        return chatRoomDao.getAllMessages()
    }
}
