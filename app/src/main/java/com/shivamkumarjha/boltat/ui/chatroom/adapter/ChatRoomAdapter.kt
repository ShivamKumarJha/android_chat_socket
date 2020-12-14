package com.shivamkumarjha.boltat.ui.chatroom.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shivamkumarjha.boltat.R
import com.shivamkumarjha.boltat.config.Constants
import com.shivamkumarjha.boltat.model.Message
import com.shivamkumarjha.boltat.utility.Utility
import java.util.*

class ChatRoomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var messages: ArrayList<Message> = arrayListOf()
    private var backupList: ArrayList<Message> = messages

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        when (viewType) {
            Constants.TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sent_message, parent, false)
                return SentMessageHolder(view)
            }
            Constants.TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_received_message, parent, false)
                return ReceivedMessageHolder(view)
            }
            Constants.TYPE_IMAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sent_image, parent, false)
                return SentImageHolder(view)
            }
            Constants.TYPE_IMAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_received_photo, parent, false)
                return ReceivedImageHolder(view)
            }
            Constants.TYPE_USER -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_new_user, parent, false)
                return UserHolder(view)
            }
            Constants.TYPE_DATE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date, parent, false)
                return DateHolder(view)
            }
        }
        return SentMessageHolder(view!!)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].viewType
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (message.viewType) {
            Constants.TYPE_MESSAGE_SENT -> {
                val messageHolder = holder as SentMessageHolder
                messageHolder.message.text = message.message
            }
            Constants.TYPE_MESSAGE_RECEIVED -> {
                val messageHolder = holder as ReceivedMessageHolder
                messageHolder.name.text = message.name
                messageHolder.message.text = message.message
            }
            Constants.TYPE_IMAGE_SENT -> {
                val sentImageHolder = holder as SentImageHolder
                val bitmap: Bitmap = Utility.get().getBitmapFromString(message.image)
                sentImageHolder.image.setImageBitmap(bitmap)
            }
            Constants.TYPE_IMAGE_RECEIVED -> {
                val receivedImageHolder = holder as ReceivedImageHolder
                receivedImageHolder.name.text = message.name
                val bitmap: Bitmap = Utility.get().getBitmapFromString(message.image)
                receivedImageHolder.image.setImageBitmap(bitmap)
            }
            Constants.TYPE_USER -> {
                val userHolder = holder as UserHolder
                userHolder.message.text = message.message
            }
            Constants.TYPE_DATE -> {
                val dateHolder = holder as DateHolder
                when (val date = message.date) {
                    Utility.get().currentDate() -> dateHolder.date.text = "Today"
                    Utility.get().previousDate() -> dateHolder.date.text = "Yesterday"
                    else -> dateHolder.date.text = date
                }
            }
        }
    }

    fun setMessages(messages: ArrayList<Message>) {
        this.messages = messages
        backupList = messages
        notifyDataSetChanged()
    }

    inner class SentMessageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.sent_message_id)
    }

    inner class SentImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imageView)
    }

    inner class ReceivedMessageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name_id)
        var message: TextView = itemView.findViewById(R.id.received_message_id)
    }

    inner class ReceivedImageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imageView)
        var name: TextView = itemView.findViewById(R.id.name_id)
    }

    inner class UserHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.message_id)
    }

    inner class DateHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var date: TextView = itemView.findViewById(R.id.date_id)
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                var filteredList: ArrayList<Message> = arrayListOf()
                if (charSequence.toString().isEmpty()) {
                    filteredList = backupList
                } else {
                    for (message in messages) {
                        if (message.viewType == Constants.TYPE_MESSAGE_SENT || message.viewType == Constants.TYPE_MESSAGE_RECEIVED) {
                            if (message.message.toLowerCase(Locale.ROOT)
                                    .contains(charSequence.toString().toLowerCase(Locale.ROOT))
                            ) {
                                filteredList.add(message)
                            }
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults?
            ) {
                messages = filterResults?.values as ArrayList<Message>
                notifyDataSetChanged()
            }
        }
    }
}