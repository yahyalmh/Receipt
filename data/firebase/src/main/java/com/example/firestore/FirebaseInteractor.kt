package com.example.firestore

import com.example.data.common.Result
import com.example.data.common.model.dto.ReceiptModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FirebaseInteractor {
    fun getAllReceipts(): Flow<Result<List<DocumentSnapshot>>>
    suspend fun saveReceipts(receiptModel: ReceiptModel): Flow<Result<Boolean>>
    fun observeFirestore(): Flow<Result<List<DocumentSnapshot>>>
}

class FirebaseInteractorImpl @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : FirebaseInteractor {
    override fun getAllReceipts() = firestoreRepository.getAllReceipts()
    override fun observeFirestore() = firestoreRepository.observeFirestore()

    override suspend fun saveReceipts(receiptModel: ReceiptModel) =
        firestoreRepository.saveReceipt(receiptModel)
}
