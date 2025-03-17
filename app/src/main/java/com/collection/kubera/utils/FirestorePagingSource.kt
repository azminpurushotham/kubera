package com.collection.kubera.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirestorePagingSource(
    private val db: FirebaseFirestore,
    private val collectionPath: String,
    private val limit: Long
) : PagingSource<QuerySnapshot, DocumentSnapshot>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, DocumentSnapshot> {
        return try {
            val query = db.collection(collectionPath)
                .limit(limit)

            val currentPage = params.key ?: query.get().await() // Fetch first page or startAfter
            val list = currentPage.documents.map { it }

            val lastDocument = currentPage.documents.lastOrNull()

            LoadResult.Page(
                data = list,
                prevKey = null, // Firestore doesn't support backward pagination
                nextKey = lastDocument?.let { query.startAfter(it).get().await() }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, DocumentSnapshot>): QuerySnapshot? = null
}