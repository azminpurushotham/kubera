package com.collection.kubera.data.repository

import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.local.dao.ShopDao
import com.collection.kubera.data.mapper.toDomainCollectionModel
import com.collection.kubera.data.mapper.toDomainShop
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ReportRepositoryImpl(
    private val collectionHistoryDao: CollectionHistoryDao,
    private val shopDao: ShopDao
) : ReportRepository {

    override suspend fun getCollectionHistoryByDateRange(
        startTimestampMillis: Long,
        endTimestampMillis: Long
    ): Result<List<CollectionModel>> = withContext(Dispatchers.IO) {
        try {
            val entities = collectionHistoryDao.getByDateRange(startTimestampMillis, endTimestampMillis)
            Result.Success(entities.map { it.toDomainCollectionModel() })
        } catch (e: Exception) {
            Timber.e(e, "getCollectionHistoryByDateRange failed")
            Result.Error(e)
        }
    }

    override suspend fun getAllShops(): Result<List<Shop>> = withContext(Dispatchers.IO) {
        try {
            val entities = shopDao.getAllShops()
            Result.Success(entities.map { it.toDomainShop() })
        } catch (e: Exception) {
            Timber.e(e, "getAllShops failed")
            Result.Error(e)
        }
    }
}
