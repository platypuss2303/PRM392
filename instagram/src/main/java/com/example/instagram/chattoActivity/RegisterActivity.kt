package com.example.instagram.chattoActivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class RegisterActivity : AppCompatActivity() {

    private lateinit var createAccountButton: Button
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var alreadyHaveAccountLink: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference
    private lateinit var loadingBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        rootRef = FirebaseDatabase.getInstance().reference

        initializeFields()

        alreadyHaveAccountLink.setOnClickListener {
            sendUserToLoginActivity()
        }

        createAccountButton.setOnClickListener {
            createNewAccount()
        }
    }

    private fun createNewAccount() {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                loadingBar.setTitle("Creating New Account")
                loadingBar.setMessage("Please wait, while we wre creating new account for you...")
                loadingBar.setCanceledOnTouchOutside(true)
                loadingBar.show()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUserID = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                            rootRef.child("Users").child(currentUserID).setValue("")

                            FirebaseMessaging.getInstance().token
                                .addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val deviceToken = tokenTask.result
                                        rootRef.child("Users").child(currentUserID).child("device_token")
                                            .setValue(deviceToken)
                                    }
                                    sendUserToMainActivity()
                                    Toast.makeText(this, "Account Created Successfully...", Toast.LENGTH_SHORT).show()
                                    loadingBar.dismiss()
                                }
                        } else {
                            val message = task.exception.toString()
                            Toast.makeText(this, "Error : $message", Toast.LENGTH_SHORT).show()
                            loadingBar.dismiss()
                        }
                    }
            }
        }
    }

    private fun initializeFields() {
        createAccountButton = findViewById(R.id.register_button)
        userEmail = findViewById(R.id.register_email)
        userPassword = findViewById(R.id.register_password)
        alreadyHaveAccountLink = findViewById(R.id.already_have_account_link)
        loadingBar = ProgressDialog(this)
    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }
}
