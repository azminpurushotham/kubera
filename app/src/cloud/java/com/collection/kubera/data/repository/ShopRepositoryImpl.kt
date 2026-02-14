package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.Result
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.utils.FirestoreShopPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
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
        ).flow
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
        ).flow
    }

    override suspend fun getShopById(id: String): Result<Shop?> {
        return try {
            val doc = firestore.collection(SHOP_COLLECTION).document(id).get().await()
            val shop = doc.toObject(Shop::class.java)?.apply { this.id = id }
            Result.Success(shop)
        } catch (e: Exception) {
            Timber.e(e, "getShopById failed")
            Result.Error(e)
        }
    }

    override suspend fun addShop(shop: Shop): Result<Shop> {
        return try {
            val docRef = firestore.collection(SHOP_COLLECTION).add(shop).await()
            shop.id = docRef.id
            Result.Success(shop)
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
