package com.collection.kubera.data.repository

import com.collection.kubera.data.local.dao.BalanceDao
import com.collection.kubera.data.local.entity.BalanceEntity
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.BalanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

private const val DEFAULT_BALANCE_ID = "default"

class BalanceRepositoryImpl(
    private val balanceDao: BalanceDao
) : BalanceRepository {

    override suspend fun getBalance(): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val entity = balanceDao.getLatest()
            Result.Success(entity?.balance ?: 0L)
        } catch (e: Exception) {
            Timber.e(e, "getBalance failed")
            Result.Error(e)
        }
    }

    override suspend fun updateBalance(delta: Long, isCredit: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = balanceDao.getLatest()
            val newBalance = when {
                entity == null -> if (isCredit) delta else -delta
                else -> if (isCredit) entity.balance + delta else entity.balance - delta
            }
            if (entity == null) {
                balanceDao.insert(
                    BalanceEntity(
                        id = DEFAULT_BALANCE_ID,
                        balance = newBalance,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } else {
                balanceDao.updateBalance(entity.id, newBalance)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateBalance failed")
            Result.Error(e)
        }
    }
}
