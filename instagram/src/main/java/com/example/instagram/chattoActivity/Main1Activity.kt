package com.example.instagram.chattoActivity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.instagram.R
import com.example.instagram.adapter.chattoAdapter.TabsAccessorAdapter
import com.example.instagram.socialActivity.SignInActivity
import com.example.instagram.socialActivity.AccountSettingActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class Main1Activity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var myViewPager: ViewPager
    private lateinit var myTabLayout: TabLayout
    private lateinit var myTabsAccessorAdapter: TabsAccessorAdapter

    private var currentUser: FirebaseUser? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
        currentUserID = mAuth.currentUser?.uid ?: ""
        rootRef = FirebaseDatabase.getInstance().reference

        mToolbar = findViewById(R.id.main_page_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "WhatsApp"

        myViewPager = findViewById(R.id.main_tabs_pager)
        myTabsAccessorAdapter = TabsAccessorAdapter(supportFragmentManager)
        myViewPager.adapter = myTabsAccessorAdapter

        myTabLayout = findViewById(R.id.main_tabs)
        myTabLayout.setupWithViewPager(myViewPager)
    }

    override fun onStart() {
        super.onStart()

        if (currentUser == null) {
            sendUserToLoginActivity()
        } else {
            updateUserStatus("online")
            verifyUserExistence()
        }
    }

    override fun onStop() {
        super.onStop()
        if (currentUser != null) {
            updateUserStatus("offline")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (currentUser != null) {
            updateUserStatus("offline")
        }
    }

    private fun verifyUserExistence() {
        val currentUserID = mAuth.currentUser?.uid ?: return

        rootRef.child("Users").child(currentUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    Toast.makeText(this@Main1Activity, "Welcome", Toast.LENGTH_SHORT).show()
                } else {
                    sendUserToSettingsActivity()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.main_logout_option -> {
                updateUserStatus("offline")
                mAuth.signOut()
                sendUserToLoginActivity()
            }
            R.id.main_settings_option -> sendUserToSettingsActivity()
            R.id.main_create_group_option -> requestNewGroup()
            R.id.main_find_friends_option -> sendUserToFindFriendsActivity()
        }

        return true
    }

    private fun requestNewGroup() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("Enter Group Name :")

        val groupNameField = EditText(this)
        groupNameField.hint = "e.g Coding Cafe"
        builder.setView(groupNameField)

        builder.setPositiveButton("Create") { _, _ ->
            val groupName = groupNameField.text.toString()

            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(this, "Please write Group Name...", Toast.LENGTH_SHORT).show()
            } else {
                createNewGroup(groupName)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun createNewGroup(groupName: String) {
        rootRef.child("Groups").child(groupName).setValue("")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "$groupName group is Created Successfully...", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this, SignInActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
    }

    private fun sendUserToSettingsActivity() {
        val settingsIntent = Intent(this, AccountSettingActivity::class.java)
        startActivity(settingsIntent)
    }

    private fun sendUserToFindFriendsActivity() {
        val findFriendsIntent = Intent(this, FindFriendsActivity::class.java)
        startActivity(findFriendsIntent)
    }

    private fun updateUserStatus(state: String) {
        val calendar = Calendar.getInstance()

        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val saveCurrentDate = currentDate.format(calendar.time)

        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val saveCurrentTime = currentTime.format(calendar.time)

        val onlineStateMap = hashMapOf<String, Any>(
            "time" to saveCurrentTime,
            "date" to saveCurrentDate,
            "state" to state
        )

        rootRef.child("Users").child(currentUserID).child("userState")
            .updateChildren(onlineStateMap)
    }
}
