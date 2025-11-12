package com.example.instagram.adapter.socialAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.socialActivity.AddStoryActivity
import com.example.instagram.R
import com.example.instagram.socialActivity.StoryViewActivity
import com.example.instagram.model.socialModel.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(
    private val context: Context,
    private val mStory: List<Story>
): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return if(viewType == 0){
          val view = LayoutInflater.from(context).inflate(R.layout.add_story_item, parent, false)
            ViewHolder(view)
       }else{
           val view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false)
             ViewHolder(view)
       }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
      val story = mStory[position]

      if(position == 0){
          // Load current user's profile image
          val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
          if (currentUserId != null) {
              val userRef = FirebaseDatabase.getInstance().reference
                  .child("Users").child(currentUserId)

              userRef.addValueEventListener(object : ValueEventListener {
                  override fun onDataChange(snapshot: DataSnapshot) {
                      if (snapshot.exists()) {
                          val imageUrl = snapshot.child("image").value.toString()
                          val username = snapshot.child("username").value.toString()

                          if (imageUrl.isNotEmpty() && holder.story_image != null) {
                              Picasso.get()
                                  .load(imageUrl)
                                  .placeholder(R.drawable.profile)
                                  .into(holder.story_image!!)
                          }

                          if (username.isNotEmpty()) {
                              holder.addStory_text?.text = username
                          }
                      }
                  }

                  override fun onCancelled(error: DatabaseError) {
                      // Handle error
                  }
              })
          }

          // Add story button click
          holder.story_plus_btn?.setOnClickListener {
              val intent = Intent(context, AddStoryActivity::class.java)
              context.startActivity(intent)
          }

          holder.itemView.setOnClickListener {
              val intent = Intent(context, AddStoryActivity::class.java)
              context.startActivity(intent)
          }
      } else {
          // View story
          userInfo(holder, story.getUserid())

          holder.itemView.setOnClickListener {
              val intent = Intent(context, StoryViewActivity::class.java)
              intent.putExtra("userid", story.getUserid())
              intent.putExtra("storyid", story.getStoryid())
              context.startActivity(intent)
          }
      }
    }

    override fun getItemViewType(position: Int) : Int {
        if(position == 0){
return 0
        }
        return  1
    }

    override fun getItemCount(): Int {
        return mStory.size
    }

    private fun userInfo(holder: ViewHolder, userId: String) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val imageUrl = snapshot.child("image").value.toString()
                    val username = snapshot.child("username").value.toString()

                    if (imageUrl.isNotEmpty() && holder.story_image != null) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.profile)
                            .into(holder.story_image!!)
                    }

                    if (username.isNotEmpty()) {
                        holder.story_username?.text = username
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    inner  class  ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var story_image_seen: ImageView? = null
        var story_image: CircleImageView? = null
        var story_username: TextView? = null

        var story_plus_btn: ImageView? = null
        var addStory_text: TextView? = null

        init{
            story_username = itemView.findViewById(R.id.story_username)
            story_image = itemView.findViewById(R.id.story_image)
            story_image_seen = itemView.findViewById(R.id.story_image_seen)

            story_plus_btn = itemView.findViewById(R.id.story_add)
            addStory_text = itemView.findViewById(R.id.add_story_text)
        }
    }
}