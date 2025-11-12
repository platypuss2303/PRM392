package com.example.instagram.fragments.chattoFragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.model.chattoModel.Contacts
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class RequestsFragment : Fragment() {
    private var RequestsFragmentView: View? = null
    private var myRequestsList: RecyclerView? = null

    private var ChatRequestsRef: DatabaseReference? = null
    private var UsersRef: DatabaseReference? = null
    private var ContactsRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var currentUserID: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth!!.currentUser!!.uid
        UsersRef = FirebaseDatabase.getInstance().reference.child("Users")
        ChatRequestsRef = FirebaseDatabase.getInstance().reference.child("Chat Requests")
        ContactsRef = FirebaseDatabase.getInstance().reference.child("Contacts")

        myRequestsList = RequestsFragmentView!!.findViewById<View>(R.id.chat_requests_list) as RecyclerView
        myRequestsList!!.layoutManager = LinearLayoutManager(context)

        return RequestsFragmentView
    }


    override fun onStart() {
        super.onStart()

        val options: FirebaseRecyclerOptions<Contacts> =
            FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestsRef!!.child(currentUserID!!), Contacts::class.java)
                .build()


        val adapter: FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> =
            object : FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                override fun onBindViewHolder(
                    holder: RequestsViewHolder,
                    position: Int,
                    model: Contacts
                ) {
                    holder.itemView.findViewById<View>(R.id.request_accept_btn).visibility = View.VISIBLE
                    holder.itemView.findViewById<View>(R.id.request_cancel_btn).visibility = View.VISIBLE

                    val list_user_id: String = getRef(position).key ?: return

                    val getTypeRef: DatabaseReference = getRef(position).child("request_type").ref

                    getTypeRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val type = dataSnapshot.value.toString()

                                if (type == "received") {
                                    UsersRef!!.child(list_user_id)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestProfileImage =
                                                        dataSnapshot.child("image").value.toString()

                                                    Picasso.get().load(requestProfileImage)
                                                        .into(holder.profileImage)
                                                }

                                                val requestUserName =
                                                    dataSnapshot.child("name").value.toString()

                                                holder.userName.text = requestUserName
                                                holder.userStatus.text = "wants to connect with you."


                                                holder.itemView.setOnClickListener {
                                                    val options: Array<CharSequence> = arrayOf(
                                                        "Accept",
                                                        "Cancel"
                                                    )

                                                    val builder = AlertDialog.Builder(requireContext())
                                                    builder.setTitle("$requestUserName  Chat Request")

                                                    builder.setItems(options) { _, i ->
                                                        if (i == 0) {
                                                            ContactsRef!!.child(currentUserID!!)
                                                                .child(list_user_id)
                                                                .child("Contact")
                                                                .setValue("Saved")
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        ContactsRef!!.child(list_user_id)
                                                                            .child(currentUserID!!)
                                                                            .child("Contact")
                                                                            .setValue("Saved")
                                                                            .addOnCompleteListener { task2 ->
                                                                                if (task2.isSuccessful) {
                                                                                    ChatRequestsRef!!.child(currentUserID!!)
                                                                                        .child(list_user_id)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener { task3 ->
                                                                                            if (task3.isSuccessful) {
                                                                                                ChatRequestsRef!!.child(list_user_id)
                                                                                                    .child(currentUserID!!)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener { task4 ->
                                                                                                        if (task4.isSuccessful) {
                                                                                                            Toast.makeText(
                                                                                                                context,
                                                                                                                "New Contact Saved",
                                                                                                                Toast.LENGTH_SHORT
                                                                                                            ).show()
                                                                                                        }
                                                                                                    }
                                                                                            }
                                                                                        }
                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                        }
                                                        if (i == 1) {
                                                            ChatRequestsRef!!.child(currentUserID!!)
                                                                .child(list_user_id)
                                                                .removeValue()
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        ChatRequestsRef!!.child(list_user_id)
                                                                            .child(currentUserID!!)
                                                                            .removeValue()
                                                                            .addOnCompleteListener { task2 ->
                                                                                if (task2.isSuccessful) {
                                                                                    Toast.makeText(
                                                                                        context,
                                                                                        "Contact Deleted",
                                                                                        Toast.LENGTH_SHORT
                                                                                    ).show()
                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                        }
                                                    }
                                                    builder.show()
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                            }
                                        })
                                } else if (type == "sent") {
                                    val request_sent_btn =
                                        holder.itemView.findViewById<Button>(R.id.request_accept_btn)
                                    request_sent_btn.text = "Req Sent"

                                    holder.itemView.findViewById<View>(R.id.request_cancel_btn).visibility = View.INVISIBLE

                                    UsersRef!!.child(list_user_id)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestProfileImage =
                                                        dataSnapshot.child("image").value.toString()

                                                    Picasso.get().load(requestProfileImage)
                                                        .into(holder.profileImage)
                                                }

                                                val requestUserName =
                                                    dataSnapshot.child("name").value.toString()

                                                holder.userName.text = requestUserName
                                                holder.userStatus.text = "you have sent a request to $requestUserName"


                                                holder.itemView.setOnClickListener {
                                                    val options: Array<CharSequence> = arrayOf(
                                                        "Cancel Chat Request"
                                                    )

                                                    val builder = AlertDialog.Builder(requireContext())
                                                    builder.setTitle("Already Sent Request")

                                                    builder.setItems(options) { _, i ->
                                                        if (i == 0) {
                                                            ChatRequestsRef!!.child(currentUserID!!)
                                                                .child(list_user_id)
                                                                .removeValue()
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        ChatRequestsRef!!.child(list_user_id)
                                                                            .child(currentUserID!!)
                                                                            .removeValue()
                                                                            .addOnCompleteListener { task2 ->
                                                                                if (task2.isSuccessful) {
                                                                                    Toast.makeText(
                                                                                        context,
                                                                                        "you have cancelled the chat request.",
                                                                                        Toast.LENGTH_SHORT
                                                                                    ).show()
                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                        }
                                                    }
                                                    builder.show()
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                            }
                                        })
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })
                }

                override fun onCreateViewHolder(
                    viewGroup: ViewGroup,
                    i: Int
                ): RequestsViewHolder {
                    val view: View = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.users_display_layout, viewGroup, false)
                    return RequestsViewHolder(view)
                }
            }

        myRequestsList!!.adapter = adapter
        adapter.startListening()
    }


    class RequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.user_profile_name)
        var userStatus: TextView = itemView.findViewById(R.id.user_status)
        var profileImage: CircleImageView? = itemView.findViewById(R.id.users_profile_image)
        var AcceptButton: Button? = itemView.findViewById(R.id.request_accept_btn)
        var CancelButton: Button? = itemView.findViewById(R.id.request_cancel_btn)
    }
}
