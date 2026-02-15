package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.TodaysCollectionData
import com.collection.kubera.data.local.dao.TodaysCollectionDao
import com.collection.kubera.data.local.entity.TodaysCollectionEntity
import com.collection.kubera.data.local.mapper.toTodaysCollectionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import java.util.UUID

private const val DEFAULT_TODAYS_ID = "default"

class TodaysCollectionRepositoryImpl(
    private val todaysCollectionDao: TodaysCollectionDao
) : TodaysCollectionRepository {

    private fun isToday(timestamp: Long): Boolean {
        val cal = Calendar.getInstance()
        val now = cal.get(Calendar.DAY_OF_YEAR) to cal.get(Calendar.YEAR)
        cal.timeInMillis = timestamp
        val then = cal.get(Calendar.DAY_OF_YEAR) to cal.get(Calendar.YEAR)
        return now == then
    }

    override suspend fun getTodaysCollection(): Result<TodaysCollectionData> = withContext(Dispatchers.IO) {
        try {
            val entity = todaysCollectionDao.getLatest()
            if (entity == null || !isToday(entity.timestamp)) {
                Result.Success(TodaysCollectionData(0L, 0L, 0L))
            } else {
                Result.Success(entity.toTodaysCollectionData())
            }
        } catch (e: Exception) {
            Timber.e(e, "getTodaysCollection failed")
            Result.Error(e)
        }
    }

    override suspend fun syncTodaysCollection(): Result<TodaysCollectionData> = withContext(Dispatchers.IO) {
        try {
            val entity = todaysCollectionDao.getLatest()
            if (entity == null) {
                Result.Success(TodaysCollectionData(0L, 0L, 0L))
            } else if (!isToday(entity.timestamp)) {
                Timber.i("Date changed, clearing previous days collection")
                todaysCollectionDao.deleteAll()
                Result.Success(TodaysCollectionData(0L, 0L, 0L))
            } else {
                Result.Success(entity.toTodaysCollectionData())
            }
        } catch (e: Exception) {
            Timber.e(e, "syncTodaysCollection failed")
            Result.Error(e)
        }
    }

    override suspend fun updateOrInsertTodaysCollection(delta: Long, isCredit: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var entity = todaysCollectionDao.getLatest()
            if (entity == null || !isToday(entity.timestamp)) {
                entity = TodaysCollectionEntity(
                    id = DEFAULT_TODAYS_ID,
                    balance = if (isCredit) delta else -delta,
                    credit = if (isCredit) delta else 0L,
                    debit = if (isCredit) 0L else -delta,
                    timestamp = System.currentTimeMillis()
                )
                todaysCollectionDao.insert(entity)
            } else {
                val balanceDelta = if (isCredit) delta else -delta
                val newBalance = entity.balance + balanceDelta
                val newCredit = if (isCredit) entity.credit + delta else entity.credit
                val newDebit = if (isCredit) entity.debit else entity.debit - delta
                todaysCollectionDao.update(entity.id, newBalance, newCredit, newDebit)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateOrInsertTodaysCollection failed")
            Result.Error(e)
        }
    }
}
