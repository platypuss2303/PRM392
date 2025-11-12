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

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingBar: ProgressDialog
    private lateinit var loginButton: Button
    private lateinit var phoneLoginButton: Button
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var needNewAccountLink: TextView
    private lateinit var forgetPasswordLink: TextView
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        initializeFields()

        needNewAccountLink.setOnClickListener {
            sendUserToRegisterActivity()
        }

        loginButton.setOnClickListener {
            allowUserToLogin()
        }

        phoneLoginButton.setOnClickListener {
            val phoneLoginIntent = Intent(this, PhoneLoginActivity::class.java)
            startActivity(phoneLoginIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            sendUserToMainActivity()
        }
    }

    private fun allowUserToLogin() {
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
                loadingBar.setTitle("Sign In")
                loadingBar.setMessage("Please wait....")
                loadingBar.setCanceledOnTouchOutside(true)
                loadingBar.show()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUserId = mAuth.currentUser?.uid ?: return@addOnCompleteListener

                            FirebaseMessaging.getInstance().token
                                .addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val deviceToken = tokenTask.result

                                        usersRef.child(currentUserId).child("device_token")
                                            .setValue(deviceToken)
                                            .addOnCompleteListener { saveTask ->
                                                if (saveTask.isSuccessful) {
                                                    sendUserToMainActivity()
                                                    Toast.makeText(this, "Logged in Successful...", Toast.LENGTH_SHORT).show()
                                                    loadingBar.dismiss()
                                                }
                                            }
                                    } else {
                                        sendUserToMainActivity()
                                        Toast.makeText(this, "Logged in Successful...", Toast.LENGTH_SHORT).show()
                                        loadingBar.dismiss()
                                    }
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
        loginButton = findViewById(R.id.login_button)
        phoneLoginButton = findViewById(R.id.phone_login_button)
        userEmail = findViewById(R.id.login_email)
        userPassword = findViewById(R.id.login_password)
        needNewAccountLink = findViewById(R.id.need_new_account_link)
        forgetPasswordLink = findViewById(R.id.forget_password_link)
        loadingBar = ProgressDialog(this)
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun sendUserToRegisterActivity() {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }
}
