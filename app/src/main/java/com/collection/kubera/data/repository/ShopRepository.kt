package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.utils.FirestorePagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

interface ShopRepository {
    fun getShopsPagingFlow(): Flow<PagingData<DocumentSnapshot>>
    fun getShopsSearchPagingFlow(shopName: String): Flow<PagingData<DocumentSnapshot>>
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
}
