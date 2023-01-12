package com.example.firestore

import android.net.Uri
import com.example.data.common.Result
import com.example.data.common.Result.Success
import com.example.data.common.model.dto.ReceiptModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

interface FirestoreRepository {
    suspend fun saveReceipt(receiptModel: ReceiptModel): Flow<Result<Boolean>>
    suspend fun getAllReceipts(): SharedFlow<Result<List<DocumentSnapshot>>>
    suspend fun observeFirestore(): SharedFlow<Result<List<DocumentSnapshot>>>
}

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : FirestoreRepository {
    private val receiptCollectionName = "receipts"
    private val storagePhotosDirectoryName = "photos"
    private val savingFlow = MutableSharedFlow<Result<Boolean>>(extraBufferCapacity = 1)

    override suspend fun observeFirestore() = coroutineScope {
        val resultFlow = MutableSharedFlow<Result<List<DocumentSnapshot>>>(extraBufferCapacity = 1)
        launch(Dispatchers.IO) {
            firestore.collection(receiptCollectionName).addSnapshotListener { snapshot, e ->
                if (e != null) {
                   resultFlow.tryEmit(Result.Error(e))
                } else {
                    snapshot?.let{
                        resultFlow.tryEmit(Success(it.documents))
                    }
                }
            }
        }
        resultFlow.asSharedFlow()
    }

    override suspend fun getAllReceipts() = coroutineScope {
        val resultFlow = MutableSharedFlow<Result<List<DocumentSnapshot>>>(extraBufferCapacity = 1)
        launch(Dispatchers.IO) {
            firestore.collection(receiptCollectionName).get().apply {
                addOnSuccessListener {
                    resultFlow.tryEmit(Success(it.documents))
                }
                addOnFailureListener { savingFlow.tryEmit(Result.Error(it)) }
            }
        }
        resultFlow.asSharedFlow()
    }

    override suspend fun saveReceipt(receiptModel: ReceiptModel) =
        coroutineScope {
            val document = if (receiptModel.id.isBlank()) {
                firestore.collection(receiptCollectionName).document()
            } else {
                firestore.collection(receiptModel.id).document()
            }
            launch(Dispatchers.IO) {
                with(document) {
                    receiptModel.id = id
                    set(receiptModel).run {
                        addOnSuccessListener {
                            uploadPhoto(document, receiptModel)
                        }
                        addOnFailureListener { savingFlow.tryEmit(Result.Error(it)) }
                    }
                }
            }
            savingFlow.asSharedFlow()
        }

    private fun uploadPhoto(documentRef: DocumentReference, receiptModel: ReceiptModel) {
        val photoLocalUri = Uri.fromFile(receiptModel.photo?.localUri?.let { File(it) })
        val reference = firebaseStorage.reference.child("$storagePhotosDirectoryName/${photoLocalUri.lastPathSegment}")
        // ToDo we can pause, cancel, resume the upload file process
        reference.putFile(photoLocalUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            reference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                receiptModel.photo?.remoteUri = task.result.toString()
                documentRef.set(receiptModel)
                savingFlow.tryEmit(Success(true))
            } else {
                savingFlow.tryEmit(Result.Error(task.exception))
            }
        }
    }
}