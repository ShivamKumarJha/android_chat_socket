package com.shivamkumarjha.boltat.ui.chatroom.listener

import com.shivamkumarjha.boltat.model.Message

interface SocketListener {
    fun onSocketMessage(message: Message)
    fun onSocketFailure(t: Throwable)
    fun onSocketClosed(reason: String)
}