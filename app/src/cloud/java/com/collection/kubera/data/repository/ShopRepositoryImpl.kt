package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.mapper.toDataShop
import com.collection.kubera.data.mapper.toDomainShop
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.ShopRepository
import com.collection.kubera.utils.FirestoreShopPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ShopRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ShopRepository {

    private val baseQuery: Query
        get() = firestore.collection(SHOP_COLLECTION)

    override fun getShopsPagingFlow(): Flow<PagingData<Shop>> {
        Timber.tag("ShopRepository").d("Creating pager")
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.SHOP_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { FirestoreShopPagingSource(query = baseQuery) }
        ).flow.map { pagingData -> pagingData.map { it.toDomainShop() } }
    }

    override fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<Shop>> {
        val searchQuery = firestore.collection(SHOP_COLLECTION)
            .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
        Timber.tag("getShopsSearch").i("Search query for: $shopName")
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.SHOP_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { FirestoreShopPagingSource(query = searchQuery) }
        ).flow.map { pagingData -> pagingData.map { it.toDomainShop() } }
    }

    override suspend fun getShopById(id: String): Result<Shop?> {
        return try {
            val doc = firestore.collection(SHOP_COLLECTION).document(id).get().await()
            val dataShop = doc.toObject(com.collection.kubera.data.Shop::class.java)?.apply { this.id = id }
            Result.Success(dataShop?.toDomainShop())
        } catch (e: Exception) {
            Timber.e(e, "getShopById failed")
            Result.Error(e)
        }
    }

    override suspend fun addShop(shop: Shop): Result<Shop> {
        return try {
            val dataShop = shop.toDataShop()
            val docRef = firestore.collection(SHOP_COLLECTION).add(dataShop).await()
            Result.Success(shop.copy(id = docRef.id))
        } catch (e: Exception) {
            Timber.e(e, "addShop failed")
            Result.Error(e)
        }
    }

    override suspend fun updateShop(shopId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            Timber.d("updateShop shopId=$shopId")
            firestore.collection(SHOP_COLLECTION).document(shopId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateShop failed")
            Result.Error(e)
        }
    }

    override suspend fun updateShopBalance(id: String, balance: Long): Result<Unit> {
        return try {
            firestore.collection(SHOP_COLLECTION).document(id)
                .update("balance", balance).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateShopBalance failed")
            Result.Error(e)
        }
    }
}
