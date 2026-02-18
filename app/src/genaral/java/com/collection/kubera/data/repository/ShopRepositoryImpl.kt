package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.local.dao.ShopDao
import com.collection.kubera.data.mapper.toDomainShop
import com.collection.kubera.data.mapper.toShopEntity
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.ShopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

class ShopRepositoryImpl(
    private val shopDao: ShopDao
) : ShopRepository {

    override fun getShopsPagingFlow(): Flow<PagingData<Shop>> {
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.SHOP_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { shopDao.getShopsPagingSource() }
        ).flow.map { pagingData -> pagingData.map { it.toDomainShop() } }
    }

    override fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<Shop>> {
        Timber.d("getShopsSearch query=$shopName")
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.SHOP_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { shopDao.getShopsSearchPagingSource(shopName.lowercase()) }
        ).flow.map { pagingData -> pagingData.map { it.toDomainShop() } }
    }

    override suspend fun getShopById(id: String): Result<Shop?> = withContext(Dispatchers.IO) {
        try {
            val entity = shopDao.getById(id).firstOrNull()
            Result.Success(entity?.toDomainShop())
        } catch (e: Exception) {
            Timber.e(e, "getShopById failed")
            Result.Error(e)
        }
    }

    override suspend fun addShop(shop: Shop): Result<Shop> = withContext(Dispatchers.IO) {
        try {
            val id = shop.id.ifEmpty { UUID.randomUUID().toString() }
            val entity = shop.copy(id = id).toShopEntity()
            shopDao.insert(entity)
            Result.Success(shop.copy(id = id))
        } catch (e: Exception) {
            Timber.e(e, "addShop failed")
            Result.Error(e)
        }
    }

    override suspend fun updateShop(shopId: String, updates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = shopDao.getById(shopId).firstOrNull() ?: return@withContext Result.Error(Exception("Shop not found"))
            val updated = entity.copy(
                shopName = updates["shopName"] as? String ?: entity.shopName,
                firstName = updates["firstName"] as? String ?: entity.firstName,
                lastName = updates["lastName"] as? String ?: entity.lastName,
                phoneNumber = updates["phoneNumber"] as? String ?: entity.phoneNumber,
                location = updates["location"] as? String ?: entity.location,
                balance = (updates["balance"] as? Number)?.toLong() ?: entity.balance,
            )
            shopDao.update(updated)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateShop failed")
            Result.Error(e)
        }
    }

    override suspend fun updateShopBalance(id: String, balance: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            shopDao.updateBalance(id, balance)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateShopBalance failed")
            Result.Error(e)
        }
    }
}
