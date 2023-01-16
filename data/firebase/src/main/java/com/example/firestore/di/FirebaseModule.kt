package com.example.firestore.di

import com.example.firestore.FirebaseInteractor
import com.example.firestore.FirebaseInteractorImpl
import com.example.firestore.FirestoreRepository
import com.example.firestore.FirestoreRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface FirebaseModule {
    companion object {
        @Provides
        @ViewModelScoped
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        }

        @Provides
        @ViewModelScoped
        fun provideFirebaseStorageRef() = FirebaseStorage.getInstance()
    }

    @Binds
    fun bindFirebaseRepository(firestoreRepositoryImpl: FirestoreRepositoryImpl): FirestoreRepository

    @Binds
    fun bindFirebaseInteractor(firebaseInteractorImpl: FirebaseInteractorImpl): FirebaseInteractor
}