package com.example.instagram.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.model.Comment
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit


class CommentAdapter(
    private val mContext: Context,
    private val mComment: MutableList<Comment>?,
): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment!![position]
        holder.commentTV.text = comment.getComment()
        getUserInfo(holder.imageProfile, holder.userNameCommentTV, comment.getPublisher())
        
        // Set time (you can customize this based on when the comment was posted)
        holder.timeTV.text = "Just now"
        
        // Like button click handler
        holder.likeBtn.setOnClickListener {
            Toast.makeText(mContext, "Liked comment by ${holder.userNameCommentTV.text}", Toast.LENGTH_SHORT).show()
            // TODO: Implement like functionality in Firebase
            // You can add Firebase logic here to save comment likes
        }
        
        // Reply button click handler
        holder.replyBtn.setOnClickListener {
            Toast.makeText(mContext, "Reply to ${holder.userNameCommentTV.text}", Toast.LENGTH_SHORT).show()
            // TODO: Implement reply functionality
            // You can open a reply dialog or navigate to reply screen
        }
        
        // Avatar click - go to user profile
        holder.imageProfile.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", comment.getPublisher())
            pref.apply()
            
            (mContext as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.example.instagram.fragments.ProfileFragment())
                .commit()
        }
        
        // Username click - go to user profile
        holder.userNameCommentTV.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", comment.getPublisher())
            pref.apply()
            
            (mContext as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.example.instagram.fragments.ProfileFragment())
                .commit()
        }

    }


    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var imageProfile: ImageView
        var userNameCommentTV: TextView
        var commentTV: TextView
        var likeBtn: TextView
        var replyBtn: TextView
        var timeTV: TextView

init{
    imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
    userNameCommentTV = itemView.findViewById(R.id.user_name_comment)
    commentTV = itemView.findViewById(R.id.comment_comment)
    likeBtn = itemView.findViewById(R.id.comment_like)
    replyBtn = itemView.findViewById(R.id.comment_reply)
    timeTV = itemView.findViewById(R.id.comment_time)

}
    }

    private fun getUserInfo(imageProfile: ImageView, userNameCommentTV: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisher)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
             if(p0.exists()){
                 val user = p0.getValue(User::class.java)
                 if (user != null) {
                     val imageUrl = user.getImage()
                     if (!imageUrl.isNullOrBlank()) {
                         Picasso.get()
                             .load(imageUrl)
                             .placeholder(R.drawable.profile)
                             .error(R.drawable.profile)
                             .into(imageProfile)
                     } else {
                         imageProfile.setImageResource(R.drawable.profile)
                     }
                     userNameCommentTV.text = user.getUsername()
                 }
             }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })

    }


}