package com.collection.kubera.data.repository

import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.data.mapper.toDomainCollectionModel
import com.collection.kubera.data.mapper.toDomainShop
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.ReportRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ReportRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ReportRepository {

    private val transactionCollection get() = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
    private val shopCollection get() = firestore.collection(SHOP_COLLECTION)

    override suspend fun getCollectionHistoryByDateRange(
        startTimestampMillis: Long,
        endTimestampMillis: Long
    ): Result<List<CollectionModel>> {
        return try {
            val startTimestamp = com.google.firebase.Timestamp(java.util.Date(startTimestampMillis))
            val endTimestamp = com.google.firebase.Timestamp(java.util.Date(endTimestampMillis))
            val querySnapshot = transactionCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
                .whereLessThanOrEqualTo("timestamp", endTimestamp)
                .get().await()
            val list = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(com.collection.kubera.data.CollectionModel::class.java)?.apply { id = doc.id }?.toDomainCollectionModel()
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
            val querySnapshot = shopCollection.get().await()
            val list = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(com.collection.kubera.data.Shop::class.java)?.apply { id = doc.id }?.toDomainShop()
            }
            Result.Success(list)
        } catch (e: Exception) {
            Timber.e(e, "getAllShops failed")
            Result.Error(e)
        }
    }
}
