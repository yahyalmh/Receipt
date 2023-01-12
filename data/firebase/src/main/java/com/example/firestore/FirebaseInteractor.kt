package com.example.firestore

import com.example.data.common.Result
import com.example.data.common.model.dto.ReceiptModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

interface FirebaseInteractor {
    suspend fun getAllReceipts(): SharedFlow<Result<List<DocumentSnapshot>>>
    suspend fun saveReceipts(receiptModel: ReceiptModel): Flow<Result<Boolean>>
    suspend fun observeFirestore(): SharedFlow<Result<List<DocumentSnapshot>>>
}

class FirebaseInteractorImpl @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : FirebaseInteractor {
    override suspend fun getAllReceipts() = firestoreRepository.getAllReceipts()
    override suspend fun observeFirestore() = firestoreRepository.observeFirestore()

    override suspend fun saveReceipts(receiptModel: ReceiptModel) =
        firestoreRepository.saveReceipt(receiptModel)
}
