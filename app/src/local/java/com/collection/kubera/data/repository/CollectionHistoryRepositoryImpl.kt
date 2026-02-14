package com.collection.kubera.data.repository

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.local.mapper.toCollectionHistoryEntity
import timber.log.Timber

class CollectionHistoryRepositoryImpl(
    private val collectionHistoryDao: CollectionHistoryDao
) : CollectionHistoryRepository {

    override suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit> {
        return try {
            val entity = model.toCollectionHistoryEntity()
            collectionHistoryDao.insert(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "insertCollectionHistory failed")
            Result.Error(e)
        }
    }
}
