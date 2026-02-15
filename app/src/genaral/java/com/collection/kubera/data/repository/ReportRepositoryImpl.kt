package com.collection.kubera.data.repository

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.local.dao.ShopDao
import com.collection.kubera.data.local.mapper.toCollectionModel
import com.collection.kubera.data.local.mapper.toShop
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ReportRepositoryImpl(
    private val collectionHistoryDao: CollectionHistoryDao,
    private val shopDao: ShopDao
) : ReportRepository {

    override suspend fun getCollectionHistoryByDateRange(
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): Result<List<CollectionModel>> = withContext(Dispatchers.IO) {
        try {
            val start = startTimestamp.toDate().time
            val end = endTimestamp.toDate().time
            val entities = collectionHistoryDao.getByDateRange(start, end)
            Result.Success(entities.map { it.toCollectionModel() })
        } catch (e: Exception) {
            Timber.e(e, "getCollectionHistoryByDateRange failed")
            Result.Error(e)
        }
    }

    override suspend fun getAllShops(): Result<List<Shop>> = withContext(Dispatchers.IO) {
        try {
            val entities = shopDao.getAllShops()
            Result.Success(entities.map { it.toShop() })
        } catch (e: Exception) {
            Timber.e(e, "getAllShops failed")
            Result.Error(e)
        }
    }
}
