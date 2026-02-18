package com.collection.kubera.data.repository

import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.mapper.toCollectionHistoryEntity
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.CollectionHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CollectionHistoryRepositoryImpl(
    private val collectionHistoryDao: CollectionHistoryDao
) : CollectionHistoryRepository {

    override suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = model.toCollectionHistoryEntity()
            collectionHistoryDao.insert(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "insertCollectionHistory failed")
            Result.Error(e)
        }
    }
}
