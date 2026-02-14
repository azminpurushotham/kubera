package com.collection.kubera.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.collection.kubera.data.Shop
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirestoreShopPagingSource(
    private val query: Query
) : PagingSource<Query, Shop>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, Shop> {
        return try {
            val limit = params.loadSize.toLong()
            val currentPage = params.key ?: query.limit(limit)
            val snapshot = currentPage.get().await()
            val documents = snapshot.documents
            val shops = documents.mapNotNull { doc ->
                doc.toObject(Shop::class.java)?.apply { id = doc.id }
            }
            val lastDocument = documents.lastOrNull()
            val nextKey = lastDocument?.let { query.startAfter(it).limit(limit) }
            LoadResult.Page(
                data = shops,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e(e, "FirestoreShopPagingSource load failed")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, Shop>): Query? = null
}
