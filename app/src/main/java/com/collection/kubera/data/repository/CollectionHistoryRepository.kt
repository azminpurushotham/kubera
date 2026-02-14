package com.collection.kubera.data.repository

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface CollectionHistoryRepository {
    suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit>
}

class CollectionHistoryRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CollectionHistoryRepository {

    override suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit> {
        return try {
            firestore.collection(TRANSECTION_HISTORY_COLLECTION).add(model).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "insertCollectionHistory failed")
            Result.Error(e)
        }
    }
}
