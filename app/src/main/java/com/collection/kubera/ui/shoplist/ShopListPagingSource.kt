package com.collection.kubera.ui.shoplist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.collection.kubera.data.Shop
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ShopListPagingSource(
    private val q: Query
) :
    PagingSource<DocumentSnapshot, Shop>() {
    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Shop> {
        return try {
            // Determine the query based on load type
            val pageSize = params.loadSize.toLong()
            var query = q
            val currentQuery = when (params) {
                is LoadParams.Refresh -> {
                    Timber.i("Refresh-->>")
                    query.limit(pageSize) // Initial load
                }
                is LoadParams.Append -> {
                    Timber.i("Append-->>")
                    val lastDocument = params.key ?: return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                    query.startAfter(lastDocument).limit(pageSize) // Next page
                }
                is LoadParams.Prepend -> {
                    Timber.i("Prepend-->>")
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    ) // Not supporting backward pagination here
                 }
            }

            // Fetch data from Firestore
            val snapshot = currentQuery.get().await()
            val items = snapshot.mapNotNull {
                it.toObject(Shop::class.java).apply {
                        id = it.id
                    }
            }
            Timber.i("SIZE ${items.size}")
            val lastDocument = snapshot.documents.lastOrNull()
            val nextKey = if (items.isEmpty() || items.size < pageSize) null else lastDocument
            Timber.i("nextKey $nextKey")

            // Log for debugging
            Timber.i("Loaded ${items.size} items, lastDoc: ${lastDocument?.id}")

            LoadResult.Page(
                data = items,
                prevKey = null, // Firestore typically paginates forward only
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Shop>): DocumentSnapshot? {
        // This helps refresh from the most recent anchor point
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.nextKey
        }
    }
}