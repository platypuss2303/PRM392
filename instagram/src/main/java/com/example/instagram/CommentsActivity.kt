package com.example.instagram

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.adapter.CommentAdapter
import com.example.instagram.databinding.ActivityCommentsBinding
import com.example.instagram.model.Comment
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<Comment>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        postId = intent.getStringExtra("postId")!!
        publisherId = intent.getStringExtra("publisherId")!!

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView
        recyclerView = binding.recyclerViewComments
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter


        userInfo()
        readComments()
        getPostImage()
        
        // Back button handler
        binding.backBtnComments.setOnClickListener {
            finish()
        }
        
        binding.postComment.setOnClickListener {
            if (binding.addComment.text.toString().trim().isEmpty()) {
                Toast.makeText(this@CommentsActivity, "Please write comment first.", Toast.LENGTH_LONG).show()
            } else {
                addComment()
            }
        }


    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = binding.addComment.text.toString().trim()
        commentsMap["publisher"] = firebaseUser!!.uid
        commentsRef.push().setValue(commentsMap)

        binding.addComment.text.clear()
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        val imageUrl = it.getImage()
                        if (!imageUrl.isNullOrBlank()) {
                            Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.profile)
                                .into(binding.profileImageComment)
                        } else {
                            binding.profileImageComment.setImageResource(R.drawable.profile)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val post = snapshot.getValue(com.example.instagram.model.Post::class.java)
                    post?.let {
                        val imageUrl = it.getPostimage()
                        if (!imageUrl.isNullOrBlank()) {
                            Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.profile)
                                .into(binding.postImageComment)
                        } else {
                            binding.postImageComment.setImageResource(R.drawable.profile)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun readComments(){
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)
        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    commentList!!.clear()
                    for(snapshot in p0.children){
                        val comment = snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
}