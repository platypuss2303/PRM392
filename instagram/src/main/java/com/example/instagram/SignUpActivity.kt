package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagram.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

//import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signinLinkBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


        binding.signupBtn.setOnClickListener {
            CreateAccount()
        }
    }



    private fun CreateAccount()
    {
        val fullName = binding.fullnameSignup.text.toString()
        val userName = binding.usernameSignup.text.toString()
        val email = binding.emailSignup.text.toString()
        val password = binding.passwordSignup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "full name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "user name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(fullName, userName, email, progressDialog)
                        }
                        else
                        {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }



    private fun saveUserInfo(fullName: String, userName: String, email: String, progressDialog: ProgressDialog)
    {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.lowercase()
        userMap["username"] = userName.lowercase()
        userMap["email"] = email
        userMap["bio"] = "hey i am using Coding Cafe Instagram Clone App."
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/chatto-2eb54.firebasestorage.app/o/Default%20Image%2Fprofile.png?alt=media&token=80f5ab5e-0f06-49e3-a507-8fc4b90cd4b2"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been created successfully.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}