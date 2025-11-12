package com.example.instagram.socialActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.yalantis.ucrop.UCrop
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageStoryPicRef: StorageReference? = null
    
    private lateinit var imageStory: ImageView
    private lateinit var close: ImageView
    private lateinit var postStory: TextView
    private lateinit var progressBar: ProgressBar
    
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cropLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        // Initialize Firebase Storage reference
        storageStoryPicRef = FirebaseStorage.getInstance().reference.child("Story Pictures")

        // Initialize views
        imageStory = findViewById(R.id.image_story)
        close = findViewById(R.id.close)
        postStory = findViewById(R.id.post_story)
        progressBar = findViewById(R.id.progress_bar)

        // Setup Activity Result Launchers
        setupActivityResultLaunchers()

        // Close button
        close.setOnClickListener {
            finish()
        }

        // Post story button
        postStory.setOnClickListener {
            if (imageUri != null) {
                uploadStory()
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        // Open image picker
        openImagePicker()
    }

    private fun setupActivityResultLaunchers() {
        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    startCrop(selectedImageUri)
                }
            } else {
                finish() // Close activity if no image selected
            }
        }

        // UCrop launcher
        cropLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    imageUri = resultUri
                    imageStory.setImageURI(imageUri)
                } else {
                    finish()
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                Toast.makeText(this, "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                finish()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationFileName = "cropped_story_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, destinationFileName))

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(9f, 16f) // Story aspect ratio
            .withMaxResultSize(1080, 1920)

        val intent = uCrop.getIntent(this)
        cropLauncher.launch(intent)
    }

    private fun uploadStory() {
        progressBar.visibility = View.VISIBLE
        postStory.isEnabled = false

        if (imageUri != null) {
            val fileRef = storageStoryPicRef!!.child(
                System.currentTimeMillis().toString() + ".jpg"
            )

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()

                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                    val storyRef = FirebaseDatabase.getInstance().reference
                        .child("Story")
                        .child(currentUserId)

                    val storyId = storyRef.push().key
                    val timeEnd = System.currentTimeMillis() + 86400000 // 24 hours from now

                    val storyMap = HashMap<String, Any>()
                    storyMap["imageurl"] = myUrl
                    storyMap["timestart"] = ServerValue.TIMESTAMP
                    storyMap["storyid"] = storyId!!
                    storyMap["userid"] = currentUserId
                    storyMap["timeend"] = timeEnd

                    storyRef.child(storyId).updateChildren(storyMap)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    "Story uploaded successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                progressBar.visibility = View.GONE
                                postStory.isEnabled = true
                                Toast.makeText(
                                    this,
                                    "Failed to upload story: ${task2.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    progressBar.visibility = View.GONE
                    postStory.isEnabled = true
                    Toast.makeText(
                        this,
                        "Failed to upload image: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            progressBar.visibility = View.GONE
            postStory.isEnabled = true
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
}
