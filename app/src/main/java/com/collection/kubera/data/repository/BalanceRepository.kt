package com.collection.kubera.data.repository

import com.collection.kubera.data.BALANCE_COLLECTION
import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface BalanceRepository {
    suspend fun getBalance(): Result<Long>
    suspend fun updateBalance(delta: Long, isCredit: Boolean): Result<Unit>
}

class BalanceRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : BalanceRepository {

    override suspend fun getBalance(): Result<Long> {
        return try {
            val querySnapshot = firestore.collection(BALANCE_COLLECTION).get().await()
            val balances = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(BalanceAmount::class.java)?.apply { id = doc.id }
            }
            val balance = if (balances.isNotEmpty() && balances[0].balance > 0) {
                balances[0].balance
            } else {
                0L
            }
            Result.Success(balance)
        } catch (e: Exception) {
            Timber.e(e, "getBalance failed")
            Result.Error(e)
        }
    }

    override suspend fun updateBalance(delta: Long, isCredit: Boolean): Result<Unit> {
        return try {
            val querySnapshot = firestore.collection(BALANCE_COLLECTION).get().await()
            val balances = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(BalanceAmount::class.java)?.apply { id = doc.id }
            }
            if (balances.isEmpty()) return Result.Success(Unit)
            val current = balances.first()
            val newBalance = if (isCredit) current.balance + delta else current.balance - delta
            current.id?.let { docId ->
                firestore.collection(BALANCE_COLLECTION).document(docId)
                    .update(mapOf("balance" to newBalance)).await()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateBalance failed")
            Result.Error(e)
        }
    }
}
