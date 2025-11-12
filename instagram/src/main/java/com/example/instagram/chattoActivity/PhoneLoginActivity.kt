package com.example.instagram.chattoActivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {
    private var SendVerificationCodeButton: Button? = null
    private var VerifyButton: Button? = null
    private var InputPhoneNumber: EditText? = null
    private var InputVerificationCode: EditText? = null

    private var callbacks: OnVerificationStateChangedCallbacks? = null
    private var mAuth: FirebaseAuth? = null

    private var loadingBar: ProgressDialog? = null

    private var mVerificationId: String? = null
    private var mResendToken: ForceResendingToken? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)


        mAuth = FirebaseAuth.getInstance()


        SendVerificationCodeButton = findViewById<View?>(R.id.send_ver_code_button) as Button
        VerifyButton = findViewById<View?>(R.id.verify_button) as Button
        InputPhoneNumber = findViewById<View?>(R.id.phone_nnumber_input) as EditText
        InputVerificationCode = findViewById<View?>(R.id.verification_code_input) as EditText
        loadingBar = ProgressDialog(this)


        SendVerificationCodeButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val phoneNumber = InputPhoneNumber!!.getText().toString()

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "Please enter your phone number first...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    loadingBar!!.setTitle("Phone Verification")
                    loadingBar!!.setMessage("please wait, while we are authenticating your phone...")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,  // Phone number to verify
                        60,  // Timeout duration
                        TimeUnit.SECONDS,  // Unit of timeout
                        this@PhoneLoginActivity,  // Activity (for callback binding)
                        callbacks!!
                    ) // OnVerificationStateChangedCallbacks
                }
            }
        })


        VerifyButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                SendVerificationCodeButton!!.setVisibility(View.INVISIBLE)
                InputPhoneNumber!!.setVisibility(View.INVISIBLE)

                val verificationCode = InputVerificationCode!!.getText().toString()

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "Please write verification code first...",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    loadingBar!!.setTitle("Verification Code")
                    loadingBar!!.setMessage("please wait, while we are verifying verification code...")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()

                    val credential =
                        PhoneAuthProvider.getCredential(mVerificationId!!, verificationCode)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        })


        callbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException?) {
                loadingBar!!.dismiss()
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "Invalid Phone Number, Please enter correct phone number with your country code...",
                    Toast.LENGTH_SHORT
                ).show()

                SendVerificationCodeButton!!.setVisibility(View.VISIBLE)
                InputPhoneNumber!!.setVisibility(View.VISIBLE)

                VerifyButton!!.setVisibility(View.INVISIBLE)
                InputVerificationCode!!.setVisibility(View.INVISIBLE)
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken?
            ) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token

                loadingBar!!.dismiss()
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "Code has been sent, please check and verify...",
                    Toast.LENGTH_SHORT
                ).show()

                SendVerificationCodeButton!!.setVisibility(View.INVISIBLE)
                InputPhoneNumber!!.setVisibility(View.INVISIBLE)

                VerifyButton!!.setVisibility(View.VISIBLE)
                InputVerificationCode!!.setVisibility(View.VISIBLE)
            }
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful()) {
                        loadingBar!!.dismiss()
                        Toast.makeText(
                            this@PhoneLoginActivity,
                            "Congratulations, you're logged in successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                        SendUserToMainActivity()
                    } else {
                        val message = task.getException().toString()
                        Toast.makeText(
                            this@PhoneLoginActivity,
                            "Error : " + message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }


    private fun SendUserToMainActivity() {
        val mainIntent: Intent = Intent(this@PhoneLoginActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
