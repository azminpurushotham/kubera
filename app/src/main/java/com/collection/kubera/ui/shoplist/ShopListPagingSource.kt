package com.collection.kubera.ui.shoplist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore

class ShopListPagingSource(private val pageSize: Long, private val firestore: FirebaseFirestore) : PagingSource<Int, String>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        val page = params.key ?: 1 // Start from page 1
        return try {
            var query = firestore.collection("shop")
                .limit(pageSize)

            val items = List(pageSize.toInt()) { "Item ${((page - 1) * 20) + it + 1}" } // Fake data
            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1, // Previous page
                nextKey = if (items.isEmpty()) null else page + 1 // Next page
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}