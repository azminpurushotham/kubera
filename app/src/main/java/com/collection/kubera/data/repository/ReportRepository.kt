package com.collection.kubera.data.repository

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface ReportRepository {
    suspend fun getCollectionHistoryByDateRange(
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): Result<List<CollectionModel>>

    suspend fun getAllShops(): Result<List<Shop>>
}

class ReportRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ReportRepository {

    private val transactionCollection
        get() = firestore.collection(TRANSECTION_HISTORY_COLLECTION)

    private val shopCollection
        get() = firestore.collection(SHOP_COLLECTION)

    override suspend fun getCollectionHistoryByDateRange(
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): Result<List<CollectionModel>> {
        return try {
            Timber.d("getCollectionHistoryByDateRange")
            val querySnapshot = transactionCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
                .whereLessThanOrEqualTo("timestamp", endTimestamp)
                .get()
                .await()
            val list = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(CollectionModel::class.java)?.apply { id = doc.id }
                } catch (e: Exception) {
                    Timber.e(e)
                    null
                }
            }
            Result.Success(list)
        } catch (e: Exception) {
            Timber.e(e, "getCollectionHistoryByDateRange failed")
            Result.Error(e)
        }
    }

    override suspend fun getAllShops(): Result<List<Shop>> {
        return try {
            Timber.d("getAllShops")
            val querySnapshot = shopCollection.get().await()
            val list = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Shop::class.java)?.apply { id = doc.id }
            }
            Result.Success(list)
        } catch (e: Exception) {
            Timber.e(e, "getAllShops failed")
            Result.Error(e)
        }
    }
}
