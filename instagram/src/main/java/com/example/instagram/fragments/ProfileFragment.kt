package com.example.instagram.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram.AccountSettingActivity
import com.example.instagram.model.User
import com.example.instagram.R
import com.example.instagram.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.editAccountSettingsBtn.setOnClickListener {
            val buttonText = binding.editAccountSettingsBtn.text.toString()
            when (buttonText) {
                "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Follow" -> followUser()
                "Following" -> unfollowUser()
            }
        }

        getFollowers()
        getFollowings()
        userInfo()

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
                if (snapshot.child(profileId).exists()) {
                    binding.editAccountSettingsBtn.text = "Following"
                } else {
                    binding.editAccountSettingsBtn.text = "Follow"
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
                if (snapshot.exists()) {
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
                if (snapshot.exists()) {
                    binding.totalFollowing.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
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

    private fun saveProfileIdToPrefs() {
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}
