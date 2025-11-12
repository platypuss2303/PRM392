package com.example.instagram.adapter.socialAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.model.socialModel.Comment
import com.example.instagram.model.socialModel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class CommentAdapter(
    private val mContext: Context,
    private val mComment: MutableList<Comment>?,
): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment!![position]
        holder.commentTV.text = comment.getComment()
        getUserInfo(holder.imageProfile, holder.userNameCommentTV, comment.getPublisher())


    }


    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView
        var userNameCommentTV: TextView
        var commentTV: TextView

init{
    imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
    userNameCommentTV = itemView.findViewById(R.id.user_name_comment)
    commentTV = itemView.findViewById(R.id.comment_comment)

}
    }

    private fun getUserInfo(imageProfile: CircleImageView, userNameCommentTV: TextView, publisher: String) {
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