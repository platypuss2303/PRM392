package com.example.instagram.chattoActivity

import com.firebase.ui.database.FirebaseRecyclerAdapter

class FindFriendsActivity : androidx.appcompat.app.AppCompatActivity() {
    private var mToolbar: androidx.appcompat.widget.Toolbar? = null
    private var FindFriendsRecyclerList: androidx.recyclerview.widget.RecyclerView? = null
    private var UsersRef: DatabaseReference? = null


    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friends)


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users")


        FindFriendsRecyclerList =
            findViewById<android.view.View?>(R.id.find_friends_recycler_list) as androidx.recyclerview.widget.RecyclerView
        FindFriendsRecyclerList!!.setLayoutManager(
            androidx.recyclerview.widget.LinearLayoutManager(
                this
            )
        )


        mToolbar =
            findViewById<android.view.View?>(R.id.find_friends_toolbar) as androidx.appcompat.widget.Toolbar?
        setSupportActionBar(mToolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        getSupportActionBar()!!.setTitle("Find Friends")
    }


    override fun onStart() {
        super.onStart()

        val options: FirebaseRecyclerOptions<Contacts?>? =
            Builder<Contacts?>()
                .setQuery(UsersRef, Contacts::class.java)
                .build()

        val adapter: FirebaseRecyclerAdapter<Contacts?, FindFriendViewHolder?> =
            object : FirebaseRecyclerAdapter<Contacts?, FindFriendViewHolder?>(options) {
                protected override fun onBindViewHolder(
                    holder: FindFriendViewHolder,
                    position: kotlin.Int,
                    model: Contacts
                ) {
                    holder.userName.setText(model.getName())
                    holder.userStatus.setText(model.getStatus())
                    Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image)
                        .into(holder.profileImage)


                    holder.itemView.setOnClickListener(object : android.view.View.OnClickListener {
                        override fun onClick(view: android.view.View?) {
                            val visit_user_id: kotlin.String? = getRef(position).getKey()

                            val profileIntent = android.content.Intent(
                                this@FindFriendsActivity,
                                ProfileActivity::class.java
                            )
                            profileIntent.putExtra("visit_user_id", visit_user_id)
                            startActivity(profileIntent)
                        }
                    })
                }

                public override fun onCreateViewHolder(
                    viewGroup: android.view.ViewGroup,
                    i: kotlin.Int
                ): FindFriendViewHolder {
                    val view: android.view.View = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.users_display_layout, viewGroup, false)
                    val viewHolder = FindFriendViewHolder(view)
                    return viewHolder
                }
            }

        FindFriendsRecyclerList!!.setAdapter(adapter)

        adapter.startListening()
    }


    class FindFriendViewHolder(itemView: android.view.View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var userName: android.widget.TextView
        var userStatus: android.widget.TextView
        var profileImage: CircleImageView?


        init {
            userName = itemView.findViewById<android.widget.TextView>(R.id.user_profile_name)
            userStatus = itemView.findViewById<android.widget.TextView>(R.id.user_status)
            profileImage = itemView.findViewById<CircleImageView?>(R.id.users_profile_image)
        }
    }
}
