package com.collection.kubera.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirestorePagingSource(
    private val query: Query
) : PagingSource<Query, DocumentSnapshot>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, DocumentSnapshot> {
        return try {
            val limit = params.loadSize.toLong()
            Timber.tag("load").i("START LOAD SIZE ->> $limit")
            val currentPage = params.key ?: query.limit(limit)// Fetch first page or startAfter
            val document = currentPage.get().await().documents
            val list = document.map { it }
            Timber.tag("load").i("list SIZE ${list.size}")
            val lastDocument = document.lastOrNull()
            val next =  lastDocument?.let {query.startAfter(it).limit(limit)}
            LoadResult.Page(
                data = list,
                prevKey = null, // Firestore doesn't support backward pagination
                nextKey = next
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, DocumentSnapshot>): Query? =
        null
}