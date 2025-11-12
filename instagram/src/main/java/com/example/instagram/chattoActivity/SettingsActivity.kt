package com.example.instagram.chattoActivity

import com.theartofdev.edmodo.cropper.CropImage

class SettingsActivity : androidx.appcompat.app.AppCompatActivity() {
    private var UpdateAccountSettings: android.widget.Button? = null
    private var userName: EditText? = null
    private var userStatus: EditText? = null
    private var userProfileImage: CircleImageView? = null

    private var currentUserID: kotlin.String? = null
    private var mAuth: FirebaseAuth? = null
    private var RootRef: DatabaseReference? = null

    private var UserProfileImagesRef: StorageReference? = null
    private var loadingBar: ProgressDialog? = null

    private var SettingsToolBar: androidx.appcompat.widget.Toolbar? = null


    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.getCurrentUser().getUid()
        RootRef = FirebaseDatabase.getInstance().getReference()
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images")


        InitializeFields()


        userName.setVisibility(android.view.View.INVISIBLE)


        UpdateAccountSettings!!.setOnClickListener(object : android.view.View.OnClickListener {
            override fun onClick(view: android.view.View?) {
                UpdateSettings()
            }
        })


        RetrieveUserInfo()


        userProfileImage.setOnClickListener(object : android.view.View.OnClickListener {
            override fun onClick(view: android.view.View?) {
                val galleryIntent = android.content.Intent()
                galleryIntent.setAction(android.content.Intent.ACTION_GET_CONTENT)
                galleryIntent.setType("image/*")
                startActivityForResult(galleryIntent, SettingsActivity.Companion.GalleryPick)
            }
        })
    }


    private fun InitializeFields() {
        UpdateAccountSettings =
            findViewById<android.view.View?>(R.id.update_settings_button) as android.widget.Button
        userName = findViewById<android.view.View?>(R.id.set_user_name) as EditText
        userStatus = findViewById<android.view.View?>(R.id.set_profile_status) as EditText
        userProfileImage =
            findViewById<android.view.View?>(R.id.set_profile_image) as CircleImageView
        loadingBar = ProgressDialog(this)

        SettingsToolBar =
            findViewById<android.view.View?>(R.id.settings_toolbar) as androidx.appcompat.widget.Toolbar?
        setSupportActionBar(SettingsToolBar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowCustomEnabled(true)
        getSupportActionBar()!!.setTitle("Account Settings")
    }


    override fun onActivityResult(
        requestCode: kotlin.Int,
        resultCode: kotlin.Int,
        data: android.content.Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SettingsActivity.Companion.GalleryPick && resultCode == android.app.Activity.RESULT_OK && data != null) {
            val ImageUri = data.getData()

            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

            if (resultCode == android.app.Activity.RESULT_OK) {
                loadingBar.setTitle("Set Profile Image")
                loadingBar.setMessage("Please wait, your profile image is updating...")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()

                val resultUri: android.net.Uri = result.getUri()


                val filePath: StorageReference = UserProfileImagesRef.child(currentUserID + ".jpg")

                filePath.putFile(resultUri).addOnCompleteListener(object :
                    OnCompleteListener<com.google.firebase.storage.UploadTask.TaskSnapshot?> {
                    override fun onComplete(task: com.google.android.gms.tasks.Task<com.google.firebase.storage.UploadTask.TaskSnapshot?>) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                this@SettingsActivity,
                                "Profile Image uploaded Successfully...",
                                Toast.LENGTH_SHORT
                            ).show()

                            val downloaedUrl: kotlin.String? =
                                task.getResult().getDownloadUrl().toString()

                            RootRef.child("Users").child(currentUserID).child("image")
                                .setValue(downloaedUrl)
                                .addOnCompleteListener(object :
                                    OnCompleteListener<java.lang.Void?> {
                                    override fun onComplete(task: com.google.android.gms.tasks.Task<java.lang.Void?>) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(
                                                this@SettingsActivity,
                                                "Image save in Database, Successfully...",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadingBar.dismiss()
                                        } else {
                                            val message = task.getException().toString()
                                            Toast.makeText(
                                                this@SettingsActivity,
                                                "Error: " + message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadingBar.dismiss()
                                        }
                                    }
                                })
                        } else {
                            val message = task.getException().toString()
                            Toast.makeText(
                                this@SettingsActivity,
                                "Error: " + message,
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingBar.dismiss()
                        }
                    }
                })
            }
        }
    }


    private fun UpdateSettings() {
        val setUserName = userName.getText().toString()
        val setStatus = userStatus.getText().toString()

        if (android.text.TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your user name first....", Toast.LENGTH_SHORT).show()
        }
        if (android.text.TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write your status....", Toast.LENGTH_SHORT).show()
        } else {
            val profileMap = java.util.HashMap<kotlin.String?, kotlin.Any?>()
            profileMap.put("uid", currentUserID)
            profileMap.put("name", setUserName)
            profileMap.put("status", setStatus)
            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                .addOnCompleteListener(object : OnCompleteListener<java.lang.Void?> {
                    override fun onComplete(task: com.google.android.gms.tasks.Task<java.lang.Void?>) {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity()
                            Toast.makeText(
                                this@SettingsActivity,
                                "Profile Updated Successfully...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val message = task.getException().toString()
                            Toast.makeText(
                                this@SettingsActivity,
                                "Error: " + message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    }


    private fun RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild(
                            "image"
                        )))
                    ) {
                        val retrieveUserName = dataSnapshot.child("name").getValue().toString()
                        val retrievesStatus = dataSnapshot.child("status").getValue().toString()
                        val retrieveProfileImage = dataSnapshot.child("image").getValue().toString()

                        userName.setText(retrieveUserName)
                        userStatus.setText(retrievesStatus)
                        Picasso.get().load(retrieveProfileImage).into(userProfileImage)
                    } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                        val retrieveUserName = dataSnapshot.child("name").getValue().toString()
                        val retrievesStatus = dataSnapshot.child("status").getValue().toString()

                        userName.setText(retrieveUserName)
                        userStatus.setText(retrievesStatus)
                    } else {
                        userName.setVisibility(android.view.View.VISIBLE)
                        Toast.makeText(
                            this@SettingsActivity,
                            "Please set & update your profile information...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError?) {
                }
            })
    }


    private fun SendUserToMainActivity() {
        val mainIntent: android.content.Intent =
            android.content.Intent(this@SettingsActivity, MainActivity::class.java)
        mainIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    companion object {
        private const val GalleryPick = 1
    }
}
