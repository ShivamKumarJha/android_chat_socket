package com.shivamkumarjha.boltat.ui.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.WebSocket

class ChatRoomViewModelFactory(private val webSocket: WebSocket) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java))
            return ChatRoomViewModel(webSocket) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}