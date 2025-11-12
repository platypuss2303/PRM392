package com.example.instagram

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.databinding.ActivityAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.yalantis.ucrop.UCrop

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private var myUrl = ""
    private var imageUri: Uri? = null
    private lateinit var storagePostPicRef: StorageReference
    private var loadingDialog: AlertDialog? = null

    // Activity Result Launcher for uCrop final cropped image
    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            UCrop.getOutput(result.data!!)?.let { uri ->
                imageUri = uri
                binding.imagePost.setImageURI(uri)
            }
        }
    }

    // Image picker launcher (gallery)
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { source ->
            val destinationUri = Uri.fromFile(java.io.File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
            val uCropIntent = UCrop.of(source, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(800, 800)
                .getIntent(this)
            cropImageLauncher.launch(uCropIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        // Close button
        binding.closeAddPostBtn.setOnClickListener {
            finish()
        }

        // Post button (in toolbar)
        binding.saveNewPostBtn.setOnClickListener {
            uploadImage()
        }

        // Select image button
        binding.selectImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Image preview click
        binding.imagePost.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }
    private fun uploadImage() {
        when {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            binding.descriptionPost.text.toString().trim().isEmpty() -> Toast.makeText(this, "Please write a description first.", Toast.LENGTH_LONG).show()
            else -> {
                showLoading("Adding New Post", "Please wait, we are adding your picture post...")

                val fileRef = storagePostPicRef.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask: StorageTask<*> = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            hideLoading()
                            throw it
                        }
                    }
                    fileRef.downloadUrl
                }.addOnCompleteListener { task ->
                    hideLoading()
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key

                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["description"] = binding.descriptionPost.text.toString().trim()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this, "Post uploaded successfully.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun showLoading(title: String, message: String) {
        if (loadingDialog?.isShowing == true) return
        val progressBar = ProgressBar(this)
        loadingDialog = AlertDialog.Builder(this)
            .setTitle(title)
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
