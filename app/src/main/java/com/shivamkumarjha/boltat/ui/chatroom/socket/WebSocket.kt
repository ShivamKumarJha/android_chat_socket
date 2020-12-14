package com.shivamkumarjha.boltat.ui.chatroom.socket

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shivamkumarjha.boltat.config.Constants
import com.shivamkumarjha.boltat.model.Message
import com.shivamkumarjha.boltat.ui.chatroom.listener.SocketListener
import okhttp3.*
import okhttp3.WebSocket

object WebSocket {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val request = Request.Builder().url(Constants.SERVER_ADDRESS).build()

    fun getWebSocket(socketListener: SocketListener): WebSocket {
        return client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                val detailsTypeToken = object : TypeToken<Message>() {}.type
                val message = gson.fromJson<Message>(text, detailsTypeToken)
                socketListener.onSocketMessage(message)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                socketListener.onSocketFailure(t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                socketListener.onSocketClosed(reason)
            }
        })
    }
}