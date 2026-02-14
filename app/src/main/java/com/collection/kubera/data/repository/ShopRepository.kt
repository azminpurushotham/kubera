package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.Result
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.utils.FirestorePagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface ShopRepository {
    fun getShopsPagingFlow(): Flow<PagingData<DocumentSnapshot>>
    fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<DocumentSnapshot>>
    suspend fun getShopById(id: String): Result<Shop?>
    suspend fun updateShopBalance(id: String, balance: Long): Result<Unit>
}

class ShopRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ShopRepository {

    private val baseQuery: Query
        get() = firestore.collection(SHOP_COLLECTION)

    private fun createPager(query: Query): Pager<Query, DocumentSnapshot> {
        Timber.tag("ShopRepository").d("Creating pager")
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.SHOP_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FirestorePagingSource(query = query)
            }
        )
    }

    override fun getShopsPagingFlow(): Flow<PagingData<DocumentSnapshot>> {
        return createPager(baseQuery).flow
    }

    override fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<DocumentSnapshot>> {
        val searchQuery = firestore.collection(SHOP_COLLECTION)
            .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
        Timber.tag("getShopsSearch").i("Search query for: $shopName")
        return createPager(searchQuery).flow
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
