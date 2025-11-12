package com.example.instagram.fragments.chattoFragments


import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.tasks.Task

/**
 * A simple [Fragment] subclass.
 */
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
    ): View {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false)


        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.getCurrentUser().getUid()
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests")
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts")


        myRequestsList =
            RequestsFragmentView!!.findViewById<View?>(R.id.chat_requests_list) as RecyclerView
        myRequestsList!!.setLayoutManager(LinearLayoutManager(getContext()))


        return RequestsFragmentView!!
    }


    override fun onStart() {
        super.onStart()

        val options: FirebaseRecyclerOptions<Contacts?>? =
            Builder<Contacts?>()
                .setQuery(ChatRequestsRef.child(currentUserID), Contacts::class.java)
                .build()


        val adapter: FirebaseRecyclerAdapter<Contacts?, RequestsViewHolder?> =
            object : FirebaseRecyclerAdapter<Contacts?, RequestsViewHolder?>(options) {
                protected override fun onBindViewHolder(
                    holder: RequestsViewHolder,
                    position: Int,
                    model: Contacts
                ) {
                    holder.itemView.findViewById<View?>(R.id.request_accept_btn)
                        .setVisibility(View.VISIBLE)
                    holder.itemView.findViewById<View?>(R.id.request_cancel_btn)
                        .setVisibility(View.VISIBLE)


                    val list_user_id: String = getRef(position).getKey()

                    val getTypeRef: DatabaseReference =
                        getRef(position).child("request_type").getRef()

                    getTypeRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val type = dataSnapshot.getValue().toString()

                                if (type == "received") {
                                    UsersRef.child(list_user_id)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestProfileImage =
                                                        dataSnapshot.child("image").getValue()
                                                            .toString()

                                                    Picasso.get().load(requestProfileImage)
                                                        .into(holder.profileImage)
                                                }

                                                val requestUserName =
                                                    dataSnapshot.child("name").getValue().toString()
                                                val requestUserStatus =
                                                    dataSnapshot.child("status").getValue()
                                                        .toString()

                                                holder.userName.setText(requestUserName)
                                                holder.userStatus.setText("wants to connect with you.")


                                                holder.itemView.setOnClickListener(object :
                                                    View.OnClickListener {
                                                    override fun onClick(view: View?) {
                                                        val options: Array<CharSequence?>? =
                                                            arrayOf<CharSequence>(
                                                                "Accept",
                                                                "Cancel"
                                                            )

                                                        val builder =
                                                            AlertDialog.Builder(getContext())
                                                        builder.setTitle(requestUserName + "  Chat Request")

                                                        builder.setItems(
                                                            options,
                                                            object :
                                                                DialogInterface.OnClickListener {
                                                                override fun onClick(
                                                                    dialogInterface: DialogInterface?,
                                                                    i: Int
                                                                ) {
                                                                    if (i == 0) {
                                                                        ContactsRef.child(
                                                                            currentUserID
                                                                        ).child(list_user_id)
                                                                            .child("Contact")
                                                                            .setValue("Saved")
                                                                            .addOnCompleteListener(
                                                                                object :
                                                                                    OnCompleteListener<Void?> {
                                                                                    override fun onComplete(
                                                                                        task: Task<Void?>
                                                                                    ) {
                                                                                        if (task.isSuccessful()) {
                                                                                            ContactsRef.child(
                                                                                                list_user_id
                                                                                            ).child(
                                                                                                currentUserID
                                                                                            ).child(
                                                                                                "Contact"
                                                                                            )
                                                                                                .setValue(
                                                                                                    "Saved"
                                                                                                )
                                                                                                .addOnCompleteListener(
                                                                                                    object :
                                                                                                        OnCompleteListener<Void?> {
                                                                                                        override fun onComplete(
                                                                                                            task: Task<Void?>
                                                                                                        ) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                ChatRequestsRef.child(
                                                                                                                    currentUserID
                                                                                                                )
                                                                                                                    .child(
                                                                                                                        list_user_id
                                                                                                                    )
                                                                                                                    .removeValue()
                                                                                                                    .addOnCompleteListener(
                                                                                                                        object :
                                                                                                                            OnCompleteListener<Void?> {
                                                                                                                            override fun onComplete(
                                                                                                                                task: Task<Void?>
                                                                                                                            ) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    ChatRequestsRef.child(
                                                                                                                                        list_user_id
                                                                                                                                    )
                                                                                                                                        .child(
                                                                                                                                            currentUserID
                                                                                                                                        )
                                                                                                                                        .removeValue()
                                                                                                                                        .addOnCompleteListener(
                                                                                                                                            object :
                                                                                                                                                OnCompleteListener<Void?> {
                                                                                                                                                override fun onComplete(
                                                                                                                                                    task: Task<Void?>
                                                                                                                                                ) {
                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                        Toast.makeText(
                                                                                                                                                            getContext(),
                                                                                                                                                            "New Contact Saved",
                                                                                                                                                            Toast.LENGTH_SHORT
                                                                                                                                                        )
                                                                                                                                                            .show()
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            })
                                                                                                                                }
                                                                                                                            }
                                                                                                                        })
                                                                                                            }
                                                                                                        }
                                                                                                    })
                                                                                        }
                                                                                    }
                                                                                })
                                                                    }
                                                                    if (i == 1) {
                                                                        ChatRequestsRef.child(
                                                                            currentUserID
                                                                        ).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(
                                                                                object :
                                                                                    OnCompleteListener<Void?> {
                                                                                    override fun onComplete(
                                                                                        task: Task<Void?>
                                                                                    ) {
                                                                                        if (task.isSuccessful()) {
                                                                                            ChatRequestsRef.child(
                                                                                                list_user_id
                                                                                            ).child(
                                                                                                currentUserID
                                                                                            )
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(
                                                                                                    object :
                                                                                                        OnCompleteListener<Void?> {
                                                                                                        override fun onComplete(
                                                                                                            task: Task<Void?>
                                                                                                        ) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                Toast.makeText(
                                                                                                                    getContext(),
                                                                                                                    "Contact Deleted",
                                                                                                                    Toast.LENGTH_SHORT
                                                                                                                )
                                                                                                                    .show()
                                                                                                            }
                                                                                                        }
                                                                                                    })
                                                                                        }
                                                                                    }
                                                                                })
                                                                    }
                                                                }
                                                            })
                                                        builder.show()
                                                    }
                                                })
                                            }

                                            override fun onCancelled(databaseError: DatabaseError?) {
                                            }
                                        })
                                } else if (type == "sent") {
                                    val request_sent_btn =
                                        holder.itemView.findViewById<Button>(R.id.request_accept_btn)
                                    request_sent_btn.setText("Req Sent")

                                    holder.itemView.findViewById<View?>(R.id.request_cancel_btn)
                                        .setVisibility(
                                            View.INVISIBLE
                                        )

                                    UsersRef.child(list_user_id)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestProfileImage =
                                                        dataSnapshot.child("image").getValue()
                                                            .toString()

                                                    Picasso.get().load(requestProfileImage)
                                                        .into(holder.profileImage)
                                                }

                                                val requestUserName =
                                                    dataSnapshot.child("name").getValue().toString()
                                                val requestUserStatus =
                                                    dataSnapshot.child("status").getValue()
                                                        .toString()

                                                holder.userName.setText(requestUserName)
                                                holder.userStatus.setText("you have sent a request to " + requestUserName)


                                                holder.itemView.setOnClickListener(object :
                                                    View.OnClickListener {
                                                    override fun onClick(view: View?) {
                                                        val options: Array<CharSequence?>? =
                                                            arrayOf<CharSequence>(
                                                                "Cancel Chat Request"
                                                            )

                                                        val builder =
                                                            AlertDialog.Builder(getContext())
                                                        builder.setTitle("Already Sent Request")

                                                        builder.setItems(
                                                            options,
                                                            object :
                                                                DialogInterface.OnClickListener {
                                                                override fun onClick(
                                                                    dialogInterface: DialogInterface?,
                                                                    i: Int
                                                                ) {
                                                                    if (i == 0) {
                                                                        ChatRequestsRef.child(
                                                                            currentUserID
                                                                        ).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(
                                                                                object :
                                                                                    OnCompleteListener<Void?> {
                                                                                    override fun onComplete(
                                                                                        task: Task<Void?>
                                                                                    ) {
                                                                                        if (task.isSuccessful()) {
                                                                                            ChatRequestsRef.child(
                                                                                                list_user_id
                                                                                            ).child(
                                                                                                currentUserID
                                                                                            )
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(
                                                                                                    object :
                                                                                                        OnCompleteListener<Void?> {
                                                                                                        override fun onComplete(
                                                                                                            task: Task<Void?>
                                                                                                        ) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                Toast.makeText(
                                                                                                                    getContext(),
                                                                                                                    "you have cancelled the chat request.",
                                                                                                                    Toast.LENGTH_SHORT
                                                                                                                )
                                                                                                                    .show()
                                                                                                            }
                                                                                                        }
                                                                                                    })
                                                                                        }
                                                                                    }
                                                                                })
                                                                    }
                                                                }
                                                            })
                                                        builder.show()
                                                    }
                                                })
                                            }

                                            override fun onCancelled(databaseError: DatabaseError?) {
                                            }
                                        })
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError?) {
                        }
                    })
                }

                public override fun onCreateViewHolder(
                    viewGroup: ViewGroup,
                    i: Int
                ): RequestsViewHolder {
                    val view: View = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_display_layout, viewGroup, false)
                    val holder = RequestsViewHolder(view)
                    return holder
                }
            }

        myRequestsList!!.setAdapter(adapter)
        adapter.startListening()
    }


    class RequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView
        var userStatus: TextView
        var profileImage: CircleImageView?
        var AcceptButton: Button?
        var CancelButton: Button?


        init {
            userName = itemView.findViewById<TextView>(R.id.user_profile_name)
            userStatus = itemView.findViewById<TextView>(R.id.user_status)
            profileImage = itemView.findViewById<CircleImageView?>(R.id.users_profile_image)
            AcceptButton = itemView.findViewById<Button?>(R.id.request_accept_btn)
            CancelButton = itemView.findViewById<Button?>(R.id.request_cancel_btn)
        }
    }
}
