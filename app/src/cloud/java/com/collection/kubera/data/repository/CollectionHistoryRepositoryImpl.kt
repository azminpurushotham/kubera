package com.collection.kubera.data.repository

import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.data.mapper.toDataCollectionModel
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.CollectionHistoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class CollectionHistoryRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CollectionHistoryRepository {

    override suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit> {
        return try {
            val dataModel = model.toDataCollectionModel()
            firestore.collection(TRANSECTION_HISTORY_COLLECTION).add(dataModel).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "insertCollectionHistory failed")
            Result.Error(e)
        }
    }
}
