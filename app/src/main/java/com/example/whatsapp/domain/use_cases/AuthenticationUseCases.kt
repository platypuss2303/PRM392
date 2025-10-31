package com.example.whatsapp.domain.use_cases

class AuthenticationUseCases(
    val isUserAuthenticated: IsUserAuthenticated,
    val firebaseAuthState: FirebaseAuthState,
    val firebaseSignOut: FirebaseSignOut,
    val firebaseSignIn: FirebaseSignIn,
    val firebaseSignUp: FirebaseSignUp
) {

}