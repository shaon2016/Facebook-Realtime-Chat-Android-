package com.shaon2016.firebaserealtimechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shaon2016.firebaserealtimechat.R
import com.shaon2016.firebaserealtimechat.model.MyMessage


class RvMsgAdapter(private val context: Context, private val messages: ArrayList<MyMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_sender_msg_row, parent, false)
            SentMessageHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_reciver_msg_row, parent, false)
            ReceivedMessageHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceivedMessageHolder).bind(message)
            }
            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SentMessageHolder).bind(message)
            }
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int) =
        if (Firebase.auth.currentUser!!.uid == messages[position].senderId) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }

    fun addUniquely(msgs: ArrayList<MyMessage>) {
        msgs.forEach {
            var found = false
            for (i in messages.indices) {
                val oldMsg = messages[i]
                if (oldMsg.id == it.id) {
                    found = true
                    break
                }
            }
            if (!found) messages.add(it)
            notifyDataSetChanged()
        }
    }


    private inner class SentMessageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var messageText: TextView =
            itemView.findViewById<View>(R.id.text_gchat_message_me) as TextView
        private val iv = itemView.findViewById<ImageView>(R.id.ivSender)

        fun bind(message: MyMessage) {
            if (message.message.isNotEmpty()) {
                iv.visibility = View.GONE
                messageText.visibility = View.VISIBLE
                messageText.text = message.message
            } else {
                iv.visibility = View.VISIBLE
                messageText.visibility = View.GONE

                Glide.with(context)
                    .load(message.imageUrl)
                    .into(iv)
            }

        }

    }

    private inner class ReceivedMessageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView =
            itemView.findViewById<View>(R.id.text_gchat_message_other) as TextView
        var nameText: TextView = itemView.findViewById<View>(R.id.tvName) as TextView
        private val iv = itemView.findViewById<ImageView>(R.id.ivRecv)

        fun bind(message: MyMessage) {
            messageText.text = message.message
            nameText.text = message.senderName

            if (message.message.isNotEmpty()) {
                iv.visibility = View.GONE
                messageText.visibility = View.VISIBLE
                messageText.text = message.message
            } else {
                iv.visibility = View.VISIBLE
                messageText.visibility = View.GONE

                Glide.with(context)
                    .load(message.imageUrl)
                    .into(iv)
            }
        }


    }
}