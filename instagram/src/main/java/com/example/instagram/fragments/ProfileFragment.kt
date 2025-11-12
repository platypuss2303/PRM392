package com.example.instagram.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.AccountSettingActivity
import com.example.instagram.model.User
import com.example.instagram.R
import com.example.instagram.adapter.MyImagesAdapter
import com.example.instagram.databinding.FragmentProfileBinding
import com.example.instagram.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.util.Collections

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private var postList: ArrayList<Post> = ArrayList()
    private var myImagesAdapter: MyImagesAdapter? = null
    private var myImagesAdapterSavedImg: MyImagesAdapter? = null
    private var postListSaved: ArrayList<Post> = ArrayList()
    private var mySavesImg: ArrayList<String> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileId = pref?.getString("profileId", firebaseUser.uid) ?: firebaseUser.uid

        if (profileId == firebaseUser.uid) {
            binding.editAccountSettingsBtn.text = "Edit Profile"
        } else {
            checkFollowAndFollowingButtonStatus()
        }

        // Upload
        var recyclerViewUpLoadImages: RecyclerView
        recyclerViewUpLoadImages = binding.recyclerViewUploadPic
        recyclerViewUpLoadImages.setHasFixedSize(true)
        var linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUpLoadImages.layoutManager = linearLayoutManager

        myImagesAdapter = context?.let {
            MyImagesAdapter(it, postList)
        }
        recyclerViewUpLoadImages.adapter = myImagesAdapter

        // Saved
        var recyclerViewSavedImages: RecyclerView
        recyclerViewSavedImages = binding.recyclerViewSavedPic
        recyclerViewSavedImages.setHasFixedSize(true)
        var linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        myImagesAdapterSavedImg = context?.let {
            MyImagesAdapter(it, postListSaved)
        }
        recyclerViewSavedImages.adapter = myImagesAdapterSavedImg

        var uploadedImagesBtn: ImageButton
        uploadedImagesBtn = binding.imagesGridViewBtn
        uploadedImagesBtn.setOnClickListener {
            recyclerViewUpLoadImages.visibility = View.VISIBLE
            recyclerViewSavedImages.visibility = View.GONE
        }

        var savedImagesBtn: ImageButton
        savedImagesBtn = binding.imagesSaveBtn
        savedImagesBtn.setOnClickListener {
            recyclerViewUpLoadImages.visibility = View.GONE
            recyclerViewSavedImages.visibility = View.VISIBLE
        }

        binding.totalFollowers.setOnClickListener {
            val intent = Intent(context, com.example.instagram.ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "Followers")
            startActivity(intent)
        }

        binding.totalFollowing.setOnClickListener {
            val intent = Intent(context, com.example.instagram.ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "Following")
            startActivity(intent)
        }

        binding.editAccountSettingsBtn.setOnClickListener {
            val buttonText = binding.editAccountSettingsBtn.text.toString()
            when (buttonText) {
                "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Follow" -> followUser()
                "Following" -> unfollowUser()
            }
            addNotification()
        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()



        return view
    }



    private fun followUser() {
        firebaseUser.uid.let { uid ->
            val ref = FirebaseDatabase.getInstance().reference
            ref.child("Follow").child(uid)
                .child("Following").child(profileId).setValue(true)
            ref.child("Follow").child(profileId)
                .child("Followers").child(uid).setValue(true)
        }
    }

    private fun unfollowUser() {
        firebaseUser.uid.let { uid ->
            val ref = FirebaseDatabase.getInstance().reference
            ref.child("Follow").child(uid)
                .child("Following").child(profileId).removeValue()
            ref.child("Follow").child(profileId)
                .child("Followers").child(uid).removeValue()
        }
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    if (snapshot.child(profileId).exists()) {
                        binding.editAccountSettingsBtn.text = "Following"
                    } else {
                        binding.editAccountSettingsBtn.text = "Follow"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId).child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null && snapshot.exists()) {
                    binding.totalFollowers.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getFollowings() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId).child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null && snapshot.exists()) {
                    binding.totalFollowing.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun myPhotos(){
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                postList.clear()
                if(p0.exists()){
                    for(snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        if(post != null && post.getPublisher() == profileId){
                            postList.add(post)
                        }
                    }
                    postList.reverse()
                    myImagesAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null && snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        Picasso.get()
                            .load(it.getImage())
                            .placeholder(R.drawable.profile)
                            .into(binding.proImageProfileFrag)

                        binding.profileFragmentUsername.text = it.getUsername()
                        binding.fullNameProfileFrag.text = it.getFullname()
                        binding.bioProfileFrag.text = it.getBio()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onStop() {
        super.onStop()
        saveProfileIdToPrefs()
    }

    override fun onPause() {
        super.onPause()
        saveProfileIdToPrefs()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveProfileIdToPrefs()
        _binding = null
    }

    private fun getTotalNumberOfPosts(){
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(_binding != null && p0.exists()){
                    var postCounter = 0
                    for(snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        if(post?.getPublisher() == profileId){
                            postCounter++
                        }
                    }
                    binding.totalPosts.text = postCounter.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }

        })
    }

    private fun saveProfileIdToPrefs() {
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    private fun mySaves() {
        var savesRef = FirebaseDatabase.getInstance().reference
            .child("Saves").child(firebaseUser.uid)

        savesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(snapshot in snapshot.children){
                        (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }
            }

            private fun readSavedImagesData() {
                val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                postRef.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            postListSaved.clear()
                            for(snapshot in snapshot.children){
                                val post = snapshot.getValue(Post::class.java)
                                if(post != null){
                                    for(key in mySavesImg){
                                        if(post.getPostid() == key){
                                            postListSaved.add(post)
                                        }
                                    }
                                }
                            }
                            myImagesAdapterSavedImg?.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database error if needed
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
    private fun addNotification(){
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "start following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)


    }
}
