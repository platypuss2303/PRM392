package com.example.instagram.socialActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.R
import com.yalantis.ucrop.UCrop
import com.example.instagram.model.socialModel.User
import com.example.instagram.databinding.ActivityAccountSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.squareup.picasso.Picasso
import java.io.File

class AccountSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingBinding
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private lateinit var storageProfilePicRef: StorageReference
    private var loadingDialog: AlertDialog? = null

    // Activity Result Launcher for uCrop final cropped image
    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            UCrop.getOutput(result.data!!)?.let { uri ->
                imageUri = uri
                binding.profileImageViewProfileFrag.setImageURI(uri)
            }
        }
    }

    // Image picker launcher (gallery)
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { source ->
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
            val uCropIntent = UCrop.of(source, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(800, 800)
                .getIntent(this)
            cropImageLauncher.launch(uCropIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val current = FirebaseAuth.getInstance().currentUser
        if (current == null) {
            // If user is not logged in, redirect to SignInActivity
            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            return
        }
        firebaseUser = current
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding.changeImageTextBtn.setOnClickListener {
            checker = "clicked"
            imagePickerLauncher.launch("image/*")
        }

        binding.saveInforProfileBtn.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(binding.fullNameProfileFrag.text.toString()) ->
                Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()

            binding.usernameProfileFrag.text.toString().isEmpty() ->
                Toast.makeText(this, "Please write user name first.", Toast.LENGTH_LONG).show()

            binding.bioProfileFrag.text.toString().isEmpty() ->
                Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()

            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>().apply {
                    put("fullname", binding.fullNameProfileFrag.text.toString().lowercase())
                    put("username", binding.usernameProfileFrag.text.toString().lowercase())
                    put("bio", binding.bioProfileFrag.text.toString().lowercase())
                }

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@AccountSettingActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        val imageUrl = it.getImage()
                        if (!imageUrl.isNullOrBlank()) {
                            Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(binding.profileImageViewProfileFrag)
                        } else {
                            binding.profileImageViewProfileFrag.setImageResource(R.drawable.ic_launcher_foreground)
                        }

                        binding.usernameProfileFrag.setText(it.getUsername())
                        binding.fullNameProfileFrag.setText(it.getFullname())
                        binding.bioProfileFrag.setText(it.getBio())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun uploadImageAndUpdateInfo() {
        when {
            imageUri == null -> {
                Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
                return
            }

            TextUtils.isEmpty(binding.fullNameProfileFrag.text.toString()) -> {
                Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
                return
            }

            binding.usernameProfileFrag.text.toString().isEmpty() -> {
                Toast.makeText(this, "Please write user name first.", Toast.LENGTH_LONG).show()
                return
            }

            binding.bioProfileFrag.text.toString().isEmpty() -> {
                Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()
                return
            }

            else -> {
                showLoading("Please wait, we are updating your profile...")

                val fileRef = storageProfilePicRef.child("${firebaseUser.uid}.jpg")
                val uploadTask: StorageTask<*> = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    fileRef.downloadUrl
                }.addOnCompleteListener { task ->
                    hideLoading()

                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>().apply {
                            put("fullname", binding.fullNameProfileFrag.text.toString().lowercase())
                            put("username", binding.usernameProfileFrag.text.toString().lowercase())
                            put("bio", binding.bioProfileFrag.text.toString().lowercase())
                            put("image", myUrl)
                        }

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(
                            this,
                            "Account Information has been updated successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(
                            Intent(
                                this@AccountSettingActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(this, "Upload failed. Please try again.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun showLoading(message: String) {
        if (loadingDialog?.isShowing == true) return
        val progressBar = ProgressBar(this)
        loadingDialog = AlertDialog.Builder(this)
            .setTitle("Account Settings")
            .setMessage(message)
            .setView(progressBar)
            .setCancelable(false)
            .create()
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}
