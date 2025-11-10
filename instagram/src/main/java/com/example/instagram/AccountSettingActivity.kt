package com.example.instagram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.example.instagram.model.User
import com.example.instagram.databinding.ActivityAccountSettingBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class AccountSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingBinding
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private lateinit var storageProfilePicRef: StorageReference
    private var loadingDialog: AlertDialog? = null

    // Activity Result Launcher cho CropImage
    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val cropResult = CropImage.getActivityResult(result.data)
            imageUri = cropResult?.uriContent
            imageUri?.let {
                binding.profileImageViewProfileFrag.setImageURI(it)
            }
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
            val intent = CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(this)
            cropImageLauncher.launch(intent)
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

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
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
