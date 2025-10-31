package com.example.whatsapp.domain.use_cases

import com.example.whatsapp.domain.repository.AuthenticationRepository
import javax.inject.Inject

class FirebaseSignUp @Inject constructor(
    private val repository: AuthenticationRepository
) {
    operator fun invoke(email: String, password: String, userName: String) = repository.firebaseSignUp(email, password, userName)
}
