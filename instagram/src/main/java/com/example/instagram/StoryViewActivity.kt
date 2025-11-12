package com.example.instagram

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryViewActivity : AppCompatActivity() {

    private lateinit var imageStoryView: ImageView
    private lateinit var storyProgress: ProgressBar
    private lateinit var storyProfileImage: CircleImageView
    private lateinit var storyUsername: TextView
    private lateinit var storyDelete: ImageView
    private lateinit var storyViewCountLayout: LinearLayout
    private lateinit var storyViewCount: TextView

    private var storyId: String = ""
    private var storyUserId: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        // Initialize views
        imageStoryView = findViewById(R.id.image_story_view)
        storyProgress = findViewById(R.id.story_progress)
        storyProfileImage = findViewById(R.id.story_profile_image)
        storyUsername = findViewById(R.id.story_username)
        storyDelete = findViewById(R.id.story_delete)
        storyViewCountLayout = findViewById(R.id.story_view_count_layout)
        storyViewCount = findViewById(R.id.story_view_count)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Get story data from intent
        storyUserId = intent.getStringExtra("userid") ?: ""
        storyId = intent.getStringExtra("storyid") ?: ""

        if (storyUserId.isEmpty() || storyId.isEmpty()) {
            Toast.makeText(this, "Error loading story", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadStory()
        loadUserInfo()
        addView()

        // Check if this is the user's own story
        if (storyUserId == currentUserId) {
            storyDelete.visibility = View.VISIBLE
            storyViewCountLayout.visibility = View.VISIBLE
            loadViewCount()
        }

        // Delete story click
        storyDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        // Close on tap
        imageStoryView.setOnClickListener {
            finish()
        }
    }

    private fun loadStory() {
        val storyRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(storyUserId).child(storyId)

        storyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val imageUrl = snapshot.child("imageurl").value.toString()
                    
                    if (imageUrl.isNotEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.profile)
                            .into(imageStoryView)
                    }
                } else {
                    Toast.makeText(this@StoryViewActivity, "Story no longer exists", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StoryViewActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUserInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(storyUserId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("username").value.toString()
                    val imageUrl = snapshot.child("image").value.toString()

                    storyUsername.text = username

                    if (imageUrl.isNotEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.profile)
                            .into(storyProfileImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun addView() {
        // Don't add view if it's the user's own story
        if (storyUserId == currentUserId) {
            return
        }

        val viewRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(storyUserId).child(storyId)
            .child("views").child(currentUserId)

        val viewMap = HashMap<String, Any>()
        viewMap["userid"] = currentUserId

        viewRef.updateChildren(viewMap)
    }

    private fun loadViewCount() {
        val viewsRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(storyUserId).child(storyId).child("views")

        viewsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val viewCount = snapshot.childrenCount
                storyViewCount.text = if (viewCount == 1L) {
                    "$viewCount view"
                } else {
                    "$viewCount views"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Story")
            .setMessage("Are you sure you want to delete this story?")
            .setPositiveButton("Delete") { _, _ ->
                deleteStory()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStory() {
        // Get the image URL first to delete from storage
        val storyRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(storyUserId).child(storyId)

        storyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val imageUrl = snapshot.child("imageurl").value.toString()

                    // Delete from database
                    storyRef.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Delete image from storage
                            if (imageUrl.isNotEmpty()) {
                                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                                imageRef.delete().addOnCompleteListener { storageTask ->
                                    if (storageTask.isSuccessful) {
                                        Toast.makeText(
                                            this@StoryViewActivity,
                                            "Story deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            finish()
                        } else {
                            Toast.makeText(
                                this@StoryViewActivity,
                                "Failed to delete story",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@StoryViewActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
