package com.shivamkumarjha.boltat.ui.chatroom

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.shivamkumarjha.boltat.config.Constants
import com.shivamkumarjha.boltat.model.Message
import com.shivamkumarjha.boltat.persistence.PreferenceManager
import com.shivamkumarjha.boltat.utility.Utility
import okhttp3.WebSocket

class ChatRoomViewModel(private val webSocket: WebSocket) : ViewModel() {

    private var localMessages: ArrayList<Message> = arrayListOf()
    private var _messages: MutableLiveData<ArrayList<Message>> =
        MutableLiveData<ArrayList<Message>>()
    val messages: LiveData<ArrayList<Message>> = _messages

    init {
        _messages.value = localMessages
    }

    fun sendTypingMessage() {
        val typingMessage = Message(
            PreferenceManager.get().userName,
            PreferenceManager.get().userName + " is typing...",
            "",
            Utility.get().currentDate(),
            Constants.TYPE_TYPING
        )
        sendMessage(typingMessage)
    }

    fun sendChatMessage(message: String) {
        val localMessage = Message(
            PreferenceManager.get().userName,
            message,
            "",
            Utility.get().currentDate(),
            Constants.TYPE_MESSAGE_SENT
        )
        addMessage(localMessage)
        val remoteMessage = Message(
            localMessage.name,
            localMessage.message,
            localMessage.image,
            Utility.get().currentDate(),
            Constants.TYPE_MESSAGE_RECEIVED
        )
        sendMessage(remoteMessage)
    }

    fun sendJoinMessage() {
        val message = Message(
            PreferenceManager.get().userName,
            PreferenceManager.get().userName + " has joined!",
            "",
            Utility.get().currentDate(),
            Constants.TYPE_USER
        )
        addMessage(message)
        sendMessage(message)
    }

    fun sendImage(bitmap: Bitmap) {
        val localMessage = Message(
            PreferenceManager.get().userName,
            "",
            Utility.get().bitmapToString(bitmap),
            Utility.get().currentDate(),
            Constants.TYPE_IMAGE_SENT
        )
        addMessage(localMessage)
        val remoteMessage = Message(
            localMessage.name,
            localMessage.message,
            localMessage.image,
            Utility.get().currentDate(),
            Constants.TYPE_IMAGE_RECEIVED
        )
        sendMessage(remoteMessage)
    }

    private fun getDateMessage(): Message {
        return Message(
            PreferenceManager.get().userName,
            "",
            "",
            Utility.get().currentDate(),
            Constants.TYPE_DATE
        )
    }

    fun addMessage(message: Message) {
        if (localMessages.isNullOrEmpty()) {
            localMessages.add(getDateMessage())
        }
        if (localMessages.last().date != Utility.get().currentDate()) {
            localMessages.add(getDateMessage())
        }
        localMessages.add(message)
        _messages.postValue(localMessages)
    }

    private fun sendMessage(message: Message) {
        webSocket.send(Gson().toJson(message))
    }
}