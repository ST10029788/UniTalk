package com.scriptsquad.unitalk.ui.chatroomactivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.scriptsquad.unitalk.viewmodel.ChatRoomViewModel
import com.scriptsquad.unitalk.R

class ChatRoomActivity : AppCompatActivity() {
    private val chatRoomViewModel: ChatRoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // Observe data from the ViewModel and update UI as needed
    }
}
