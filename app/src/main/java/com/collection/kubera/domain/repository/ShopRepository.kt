package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData

interface ShopRepository {
    fun getShopsPagingFlow(): Flow<PagingData<Shop>>
    fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<Shop>>
    suspend fun getShopById(id: String): Result<Shop?>
    suspend fun addShop(shop: Shop): Result<Shop>
    suspend fun updateShop(shopId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun updateShopBalance(id: String, balance: Long): Result<Unit>
}
