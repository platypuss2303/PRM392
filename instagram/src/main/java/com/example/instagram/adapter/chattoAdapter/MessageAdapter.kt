package com.example.instagram.adapter.chattoAdapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.model.chattoModel.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val userMessagesList: List<Messages>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessageText: TextView = itemView.findViewById(R.id.sender_messsage_text)
        val receiverMessageText: TextView = itemView.findViewById(R.id.receiver_message_text)
        val receiverProfileImage: CircleImageView = itemView.findViewById(R.id.message_profile_image)
        val messageReceiverPicture: ImageView = itemView.findViewById(R.id.message_receiver_image_view)
        val messageSenderPicture: ImageView = itemView.findViewById(R.id.message_sender_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_messages_layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageSenderId = mAuth.currentUser?.uid ?: return
        val messages = userMessagesList[position]
        val fromUserID = messages.from
        val fromMessageType = messages.type

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromUserID)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    val receiverImage = dataSnapshot.child("image").value.toString()
                    Picasso.get()
                        .load(receiverImage)
                        .placeholder(R.drawable.profile_image)
                        .into(holder.receiverProfileImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // Hide all views initially
        holder.receiverMessageText.visibility = View.GONE
        holder.receiverProfileImage.visibility = View.GONE
        holder.senderMessageText.visibility = View.GONE
        holder.messageSenderPicture.visibility = View.GONE
        holder.messageReceiverPicture.visibility = View.GONE

        if (fromMessageType == "text") {
            if (fromUserID == messageSenderId) {
                holder.senderMessageText.visibility = View.VISIBLE
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout)
                holder.senderMessageText.setTextColor(Color.BLACK)
                holder.senderMessageText.text = "${messages.message}\n \n${messages.time} - ${messages.date}"
            } else {
                holder.receiverProfileImage.visibility = View.VISIBLE
                holder.receiverMessageText.visibility = View.VISIBLE
                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout)
                holder.receiverMessageText.setTextColor(Color.BLACK)
                holder.receiverMessageText.text = "${messages.message}\n \n${messages.time} - ${messages.date}"
            }
        }
    }

    override fun getItemCount(): Int = userMessagesList.size
}
