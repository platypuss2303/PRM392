package com.example.instagram.chattoActivity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.adapter.chattoAdapter.MessageAdapter
import com.example.instagram.model.chattoModel.Messages
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar

class ChatActivity : AppCompatActivity() {
    private var messageReceiverID: String? = null
    private var messageReceiverName: String? = null
    private var messageReceiverImage: String? = null
    private var messageSenderID: String? = null

    private var userName: TextView? = null
    private var userLastSeen: TextView? = null
    private var userImage: CircleImageView? = null

    private var ChatToolBar: Toolbar? = null
    private var mAuth: FirebaseAuth? = null
    private var RootRef: DatabaseReference? = null

    private var SendMessageButton: ImageButton? = null
    private var SendFilesButton: ImageButton? = null
    private var MessageInputText: EditText? = null

    private val messagesList: MutableList<Messages> = ArrayList<Messages>()
    private var linearLayoutManager: LinearLayoutManager? = null
    private var messageAdapter: MessageAdapter? = null
    private var userMessagesList: RecyclerView? = null


    private var saveCurrentTime: String? = null
    private var saveCurrentDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        mAuth = FirebaseAuth.getInstance()
        messageSenderID = mAuth!!.currentUser!!.uid
        RootRef = FirebaseDatabase.getInstance().reference


        messageReceiverID = intent.extras!!.get("visit_user_id").toString()
        messageReceiverName = intent.extras!!.get("visit_user_name").toString()
        messageReceiverImage = intent.extras!!.get("visit_image").toString()


        IntializeControllers()


        userName!!.text = messageReceiverName
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile)
            .into(userImage)


        SendMessageButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                SendMessage()
            }
        })


        DisplayLastSeen()
    }


    private fun IntializeControllers() {
        ChatToolBar = findViewById<View?>(R.id.chat_toolbar) as Toolbar?
        setSupportActionBar(ChatToolBar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowCustomEnabled(true)

        val layoutInflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionBarView: View? = layoutInflater.inflate(R.layout.custom_chat_bar, null)
        actionBar.customView = actionBarView

        userName = findViewById<View?>(R.id.custom_profile_name) as TextView
        userLastSeen = findViewById<View?>(R.id.custom_user_last_seen) as TextView
        userImage = findViewById<View?>(R.id.custom_profile_image) as CircleImageView?

        SendMessageButton = findViewById<View?>(R.id.send_message_btn) as ImageButton
        SendFilesButton = findViewById<View?>(R.id.send_files_btn) as ImageButton
        MessageInputText = findViewById<View?>(R.id.input_message) as EditText

        messageAdapter = MessageAdapter(messagesList)
        userMessagesList = findViewById<View?>(R.id.private_messages_list_of_users) as RecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        userMessagesList!!.layoutManager = linearLayoutManager
        userMessagesList!!.adapter = messageAdapter


        val calendar = Calendar.getInstance()

        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calendar.time)

        val currentTime = SimpleDateFormat("hh:mm a")
        saveCurrentTime = currentTime.format(calendar.time)
    }


    private fun DisplayLastSeen() {
        RootRef!!.child("Users").child(messageReceiverID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child("userState").hasChild("state")) {
                        val state =
                            dataSnapshot.child("userState").child("state").value.toString()
                        val date =
                            dataSnapshot.child("userState").child("date").value.toString()
                        val time =
                            dataSnapshot.child("userState").child("time").value.toString()

                        if (state == "online") {
                            userLastSeen!!.text = "online"
                        } else if (state == "offline") {
                            userLastSeen!!.text = "Last Seen: " + date + " " + time
                        }
                    } else {
                        userLastSeen!!.text = "offline"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }


    override fun onStart() {
        super.onStart()

        RootRef!!.child("Messages").child(messageSenderID!!).child(messageReceiverID!!)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val messages: Messages? = dataSnapshot.getValue(Messages::class.java)

                    if (messages != null) {
                        messagesList.add(messages)
                    }

                    messageAdapter!!.notifyDataSetChanged()

                    userMessagesList!!.smoothScrollToPosition(
                        userMessagesList!!.adapter!!.itemCount
                    )
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }


    private fun SendMessage() {
        val messageText = MessageInputText!!.text.toString()

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show()
        } else {
            val messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID
            val messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID

            val userMessageKeyRef = RootRef!!.child("Messages")
                .child(messageSenderID!!).child(messageReceiverID!!).push()

            val messagePushID = userMessageKeyRef.key

            val messageTextBody: MutableMap<String, Any?> = HashMap<String, Any?>()
            messageTextBody.put("message", messageText)
            messageTextBody.put("type", "text")
            messageTextBody.put("from", messageSenderID)
            messageTextBody.put("to", messageReceiverID)
            messageTextBody.put("messageID", messagePushID)
            messageTextBody.put("time", saveCurrentTime)
            messageTextBody.put("date", saveCurrentDate)

            val messageBodyDetails: MutableMap<String, Any> = HashMap<String, Any>()
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody)
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody)

            RootRef!!.updateChildren(messageBodyDetails as Map<String, Any>)
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@ChatActivity,
                                "Message Sent Successfully...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@ChatActivity, "Error", Toast.LENGTH_SHORT).show()
                        }
                        MessageInputText!!.setText("")
                    }
                })
        }
    }
}
