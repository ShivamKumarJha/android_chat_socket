package com.shivamkumarjha.boltat.ui.chatroom

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shivamkumarjha.boltat.R
import com.shivamkumarjha.boltat.config.Constants
import com.shivamkumarjha.boltat.model.Message
import com.shivamkumarjha.boltat.ui.BaseApplication
import com.shivamkumarjha.boltat.ui.chatroom.adapter.ChatRoomAdapter
import com.shivamkumarjha.boltat.ui.chatroom.listener.SocketListener
import com.shivamkumarjha.boltat.utility.afterTextChanged
import com.shivamkumarjha.boltat.utility.hideKeyboard
import okhttp3.WebSocket
import java.io.FileNotFoundException

class ChatRoomActivity : BaseApplication(), SocketListener {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var imageButton: ImageView
    private lateinit var webSocket: WebSocket
    private lateinit var chatRoomViewModel: ChatRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        initializer()
    }

    private fun initializer() {
        // Toolbar
        toolbar = findViewById(R.id.toolbar_id)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        // Views
        messageEditText = findViewById(R.id.message_edit_text_id)
        sendButton = findViewById(R.id.send_button_id)
        imageButton = findViewById(R.id.image_button_id)
        // recycler view
        recyclerView = findViewById(R.id.chat_recycler_view_id)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        chatRoomAdapter = ChatRoomAdapter()
        recyclerView.adapter = chatRoomAdapter
        // View Model
        webSocket = com.shivamkumarjha.boltat.ui.chatroom.socket.WebSocket.getWebSocket(this)
        chatRoomViewModel = ViewModelProvider(this, ChatRoomViewModelFactory(webSocket))
            .get(ChatRoomViewModel::class.java)
        chatRoomViewModel.sendJoinMessage()
        chatRoomViewModel.messages.observe(this, {
            toolbar.title = resources.getString(R.string.chat_room)
            messageEditText.setText("")
            chatRoomAdapter.setMessages(it)
            recyclerView.smoothScrollToPosition(chatRoomAdapter.itemCount - 1)
        })
        // View listener
        messageEditText.afterTextChanged {
            sendButton.isEnabled = messageEditText.text.toString().isNotBlank()
            if (messageEditText.text.toString().isNotBlank()) {
                sendButton.visibility = View.VISIBLE
                imageButton.visibility = View.GONE
                chatRoomViewModel.sendTypingMessage()
            } else {
                sendButton.visibility = View.GONE
                imageButton.visibility = View.VISIBLE
            }
        }
        sendButton.setOnClickListener {
            chatRoomViewModel.sendChatMessage(messageEditText.text.toString().trim())
        }
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, resources.getString(R.string.pick_image)),
                Constants.IMAGE_REQUEST_ID
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_REQUEST_ID && resultCode == RESULT_OK) {
            try {
                val stream = contentResolver.openInputStream(data!!.data!!)
                chatRoomViewModel.sendImage(BitmapFactory.decodeStream(stream))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat_room, menu)
        val search: MenuItem = menu.findItem(R.id.search_id)
        val searchView: SearchView = search.actionView as SearchView
        val searchIcon = searchView.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)
        val cancelIcon = searchView.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.WHITE)
        val searchTextView = searchView.findViewById<TextView>(R.id.search_src_text)
        searchTextView.setTextColor(Color.WHITE)
        searchTextView.hint = resources.getString(R.string.search_messages)
        searchTextView.setHintTextColor(ContextCompat.getColor(this, R.color.purple_200))
        searchView.queryHint = resources.getString(R.string.search_messages)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                chatRoomAdapter.filter.filter(filter)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSocketMessage(message: Message) {
        runOnUiThread {
            if (message.viewType == Constants.TYPE_TYPING) {
                toolbar.title = message.message
            } else {
                chatRoomViewModel.addMessage(message)
            }
        }
    }

    override fun onSocketFailure(t: Throwable) {
        runOnUiThread {
            Toast.makeText(
                this@ChatRoomActivity,
                "Socket connection failure!\n${t.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
            this@ChatRoomActivity.finish()
        }
    }

    override fun onSocketClosed(reason: String) {
        runOnUiThread {
            Toast.makeText(
                this@ChatRoomActivity,
                "Socket connection closed!\n${reason}",
                Toast.LENGTH_LONG
            ).show()
            this@ChatRoomActivity.finish()
        }
    }
}