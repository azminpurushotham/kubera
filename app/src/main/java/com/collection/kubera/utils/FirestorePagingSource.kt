package com.collection.kubera.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirestorePagingSource : PagingSource<QuerySnapshot, DocumentSnapshot>() {
    private var query: Query? = null
    private var limit: Long? = null
    fun setPagination(
        q: Query,
        l: Long?,
    ) {
        this.query = q
        this.limit = l
    }


    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, DocumentSnapshot> {
        return try {
            Timber.tag("load").i(query.toString())
            val query = limit?.let { query?.limit(it) }

            val currentPage = params.key ?: query?.get()?.await() // Fetch first page or startAfter
            val list = currentPage?.documents?.map { it }

            val lastDocument = currentPage?.documents?.lastOrNull()

            LoadResult.Page(
                data = list!!,
                prevKey = null, // Firestore doesn't support backward pagination
                nextKey = lastDocument?.let { query?.startAfter(it)?.get()?.await() }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, DocumentSnapshot>): QuerySnapshot? =
        null
}