package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TodaysCollectionData
import com.collection.kubera.data.TodaysCollections
import com.collection.kubera.utils.formatFirestoreTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class TodaysCollectionRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TodaysCollectionRepository {

    override suspend fun getTodaysCollection(): Result<TodaysCollectionData> {
        return try {
            Timber.i("getTodaysCollection")
            val querySnapshot = firestore.collection(TODAYS_COLLECTION).get().await()
            val collections = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(TodaysCollections::class.java)?.apply { id = doc.id }
            }
            if (collections.isEmpty()) {
                Result.Success(TodaysCollectionData(0L, 0L, 0L))
            } else {
                val first = collections.first()
                Result.Success(
                    TodaysCollectionData(
                        balance = first.balance,
                        credit = first.credit,
                        debit = first.debit
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "getTodaysCollection failed")
            Result.Error(e)
        }
    }

    override suspend fun syncTodaysCollection(): Result<TodaysCollectionData> {
        return try {
            val querySnapshot = firestore.collection(TODAYS_COLLECTION).get().await()
            val collections = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(TodaysCollections::class.java)?.apply { id = doc.id }
            }
            if (collections.isEmpty()) {
                Result.Success(TodaysCollectionData(0L, 0L, 0L))
            } else {
                val first = collections.first()
                val collectionDate = formatFirestoreTimestamp(first.timestamp)
                val today = formatFirestoreTimestamp(Timestamp.now())
                if (collectionDate != today) {
                    Timber.i("Date changed, clearing previous days collection")
                    val batch = firestore.batch()
                    for (document in querySnapshot.documents) {
                        batch.delete(document.reference)
                    }
                    batch.commit().await()
                    Result.Success(TodaysCollectionData(0L, 0L, 0L))
                } else {
                    Result.Success(
                        TodaysCollectionData(
                            balance = first.balance,
                            credit = first.credit,
                            debit = first.debit
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "syncTodaysCollection failed")
            Result.Error(e)
        }
    }

    override suspend fun updateOrInsertTodaysCollection(delta: Long, isCredit: Boolean): Result<Unit> {
        return try {
            val querySnapshot = firestore.collection(TODAYS_COLLECTION).get().await()
            val collections = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(TodaysCollections::class.java)?.apply { id = doc.id }
            }
            if (collections.isEmpty()) {
                val newDoc = TodaysCollections().apply {
                    if (isCredit) {
                        credit = delta
                        balance = delta
                    } else {
                        debit = -delta
                        balance = -delta
                    }
                }
                firestore.collection(TODAYS_COLLECTION).add(newDoc).await()
            } else {
                val first = collections.first()
                val balanceDelta = if (isCredit) delta else -delta
                val prm = mutableMapOf<String, Any>("balance" to (first.balance + balanceDelta))
                if (isCredit) prm["credit"] = first.credit + delta
                else prm["debit"] = first.debit - delta
                first.id?.let { docId ->
                    firestore.collection(TODAYS_COLLECTION).document(docId).update(prm).await()
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateOrInsertTodaysCollection failed")
            Result.Error(e)
        }
    }
}
