package com.example.instagram.socialActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.adapter.socialAdapter.UserAdapter
import com.example.instagram.model.socialModel.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUsersActivity : AppCompatActivity() {
    private var id: String = ""
    private var title: String = ""
    private var userAdapter: UserAdapter? = null
    private var userList: ArrayList<User> = ArrayList()
    private var idList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        val intent = intent
        id = intent.getStringExtra("id") ?: ""
        title = intent.getStringExtra("title") ?: ""

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userAdapter = UserAdapter(this, userList, false)
        recyclerView.adapter = userAdapter

        val type = intent.getStringExtra("type") ?: ""

        when {
            title == "Likes" || type == "likes" -> getLikes()
            title == "Following" || type == "following" -> getFollowing()
            title == "Followers" || type == "followers" -> getFollowers()
            title == "Story Viewers" || type == "story_views" -> getStoryViewers()
        }
    }

    private fun getLikes() {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(id)
        
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    idList.clear()
                    for (snap in snapshot.children) {
                        snap.key?.let { idList.add(it) }
                    }
                    showUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id).child("Following")
        
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    idList.clear()
                    for (snap in snapshot.children) {
                        snap.key?.let { idList.add(it) }
                    }
                    showUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id).child("Followers")
        
        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    idList.clear()
                    for (snap in snapshot.children) {
                        snap.key?.let { idList.add(it) }
                    }
                    showUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun getStoryViewers() {
        // Get the story owner ID from the intent
        val storyUserId = intent.getStringExtra("storyUserId") ?: ""
        
        val viewsRef = if (storyUserId.isNotEmpty()) {
            FirebaseDatabase.getInstance().reference
                .child("Story").child(storyUserId).child(id).child("views")
        } else {
            // Fallback: try to get from current user
            FirebaseDatabase.getInstance().reference
                .child("Story").child(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "")
                .child(id).child("views")
        }
        
        viewsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    idList.clear()
                    for (snap in snapshot.children) {
                        snap.key?.let { idList.add(it) }
                    }
                    showUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun showUsers() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (snap in snapshot.children) {
                    val user = snap.getValue(User::class.java)
                    if (user != null) {
                        for (userId in idList) {
                            if (user.getUID() == userId) {
                                userList.add(user)
                            }
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
}