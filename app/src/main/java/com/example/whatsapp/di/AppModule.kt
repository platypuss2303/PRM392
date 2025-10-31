package com.example.whatsapp.di

import com.example.whatsapp.data.AuthenticationRepositoryImpl
import com.example.whatsapp.domain.repository.AuthenticationRepository
import com.example.whatsapp.domain.use_cases.AuthenticationUseCases
import com.example.whatsapp.domain.use_cases.FirebaseAuthState
import com.example.whatsapp.domain.use_cases.FirebaseSignIn
import com.example.whatsapp.domain.use_cases.FirebaseSignOut
import com.example.whatsapp.domain.use_cases.FirebaseSignUp
import com.example.whatsapp.domain.use_cases.IsUserAuthenticated
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthenticationRepository {
        return AuthenticationRepositoryImpl(auth =auth, firestore = firestore)


    }

    @Provides
    @Singleton
    fun provideAuthUseCases(repository: AuthenticationRepository) = AuthenticationUseCases(
    isUserAuthenticated = IsUserAuthenticated(repository = repository),
    firebaseAuthState = FirebaseAuthState(repository = repository),
    firebaseSignOut = FirebaseSignOut(repository = repository),
    firebaseSignIn = FirebaseSignIn(repository = repository),
    firebaseSignUp = FirebaseSignUp(repository = repository)

    )

}