package com.example.whatsapp.domain.use_cases

import com.example.whatsapp.domain.repository.AuthenticationRepository
import javax.inject.Inject

class FirebaseAuthState@Inject constructor(
    private val repository: AuthenticationRepository
) {
    operator fun invoke() = repository.getFirebaseAuthState()

}