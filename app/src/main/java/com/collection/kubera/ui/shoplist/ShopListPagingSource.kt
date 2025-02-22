package com.collection.kubera.ui.shoplist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ShopListPagingSource(private val pageSize: Int, private val firestore: FirebaseFirestore) :
    PagingSource<Query, Shop>() {
    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, Shop> {
        return try {
            val query = params.key ?: firestore.collection(SHOP_COLLECTION)
                .limit(pageSize.toLong())
            val snapshot = query.get().await()
            val shops = snapshot.documents.mapNotNull {
                it.toObject(Shop::class.java)
                    ?.apply {
                        id = it.id
                    }
            }
            val nextQuery = if (snapshot.documents.isNotEmpty()) {
                firestore.collection(SHOP_COLLECTION)
                    .startAfter(snapshot.documents.last())
                    .limit(pageSize.toLong())
            } else {
                null
            }
            LoadResult.Page(
                data = shops,
                prevKey = null,
                nextKey = nextQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, Shop>): Query? = null
}