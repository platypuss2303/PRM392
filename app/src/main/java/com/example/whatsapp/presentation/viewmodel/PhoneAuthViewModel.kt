package com.example.whatsapp.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.example.whatsapp.models.PhoneAuthUser
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.ByteArrayOutputStream


@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {
    private val _authState =
        MutableStateFlow<AuthState>(com.example.whatsapp.presentation.viewmodel.AuthState.Ideal)
    val authState = _authState.asStateFlow()

    private val userRef = database.reference.child("users")

    fun signInWithCredential(
        credential: PhoneAuthCredential,
        context: Context
    ) {
        _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Loading
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                task ->
            if(task.isSuccessful){
                val user = firebaseAuth.currentUser
                val phoneAuthUser = PhoneAuthUser(
                    userId = user?.uid ?: "",
                    phoneNumber = user?.phoneNumber ?: ""
                )
                markUserAsSignedIn(context)
                _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Success(phoneAuthUser)

                fetchUserProfile(user?.uid ?: "")

            }else{
                _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Error(task.exception?.message ?: "Sign in failed")

            }
        }
    }

    fun resetAuthState(){
        _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Ideal

    }

    fun signOut(activity: Activity){
        firebaseAuth.signOut()
        val sharedPreferences = activity.getSharedPreferences("app_prefs", Activity.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isSigned", false).apply()
    }

    fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveUserProfile(userId: String, name: String, status: String, profileImage: Bitmap?){
        val database = FirebaseDatabase.getInstance().reference
        val encodedImage = profileImage?.let {
            convertBitmapToBase64(it)
        }
        val userProfile = PhoneAuthUser(
            userId = userId,
            name = name,
            status = status,
            phoneNumber = Firebase.auth.currentUser?.phoneNumber?: "",
            profileImage = encodedImage,
        )

    }



    fun verifyCode(otp: String, context: Context) {
        val currentAuthState = _authState.value
        if(currentAuthState !is com.example.whatsapp.presentation.viewmodel.AuthState.CodeSent){
            Log.e("PhoneAuth", "Attempted to verify OTP without a verification ID")
            _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Error("Attempted to verify OTP without a verification ID")
            return
        }
        val credential = PhoneAuthProvider.getCredential(currentAuthState.verificationId, otp)
        signInWithCredential(credential, context)
    }
    fun senderVerificationCode(phoneNumber: String, activity: Activity) {
        _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Loading
        val option = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(id, token)
                Log.d("PhoneAuth", "onCodeSent: $id")
                _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.CodeSent(id)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredential(credential, context = activity)
            }









            override fun onVerificationFailed(exception: FirebaseException) {
                Log.e("PhoneAuth", "Verification failed: ${exception.message}")
                _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Error(exception.message ?: "Verification failed")
            }
        }
        val phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(option)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)


    }

    private fun fetchUserProfile(userId: String) {
        val userRef = userRef.child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userProfile = snapshot.getValue(PhoneAuthUser::class.java)
                if (userProfile != null) {
                    _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Success(userProfile)
                }
                }
            }.addOnFailureListener {
                _authState.value = com.example.whatsapp.presentation.viewmodel.AuthState.Error(it.message ?: "Failed to fetch user profile")
        }

    }




    private fun markUserAsSignedIn(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("is_signed_in", true).apply()
    }
}




sealed class AuthState{
    object Ideal:AuthState()
    object Loading:AuthState()
    data class CodeSent(val verificationId: String): AuthState()
    data class Success(val user: PhoneAuthUser): AuthState()
    data class Error(val message: String): AuthState()
}