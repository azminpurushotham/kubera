package com.collection.kubera.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.collection.kubera.data.CollectionModel
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirestoreCollectionPagingSource(
    private val query: Query
) : PagingSource<Query, CollectionModel>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, CollectionModel> {
        return try {
            val limit = params.loadSize.toLong()
            val currentPage = params.key ?: query.limit(limit)
            val snapshot = currentPage.get().await()
            val documents = snapshot.documents
            val models = documents.mapNotNull { doc ->
                try {
                    doc.toObject(CollectionModel::class.java)?.apply { id = doc.id }
                } catch (e: Exception) {
                    Timber.e(e)
                    null
                }
            }
            val lastDocument = documents.lastOrNull()
            val nextKey = lastDocument?.let { query.startAfter(it).limit(limit) }
            LoadResult.Page(
                data = models,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e(e, "FirestoreCollectionPagingSource load failed")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, CollectionModel>): Query? = null
}
