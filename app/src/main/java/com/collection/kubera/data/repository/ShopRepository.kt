package com.collection.kubera.data.repository

import androidx.paging.PagingData
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getShopsPagingFlow(): Flow<PagingData<Shop>>
    fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<Shop>>
    suspend fun getShopById(id: String): Result<Shop?>
    suspend fun addShop(shop: Shop): Result<Shop>
    suspend fun updateShop(shopId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun updateShopBalance(id: String, balance: Long): Result<Unit>
}
