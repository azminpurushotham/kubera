package com.collection.kubera.ui.orderhistory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderHistoryPagingSource(private val pageSize: Int, private val firestore: FirebaseFirestore) :
    PagingSource<Query, CollectionHistory>() {
    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, CollectionHistory> {
        return try {
            val query = params.key ?: firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .limit(pageSize.toLong())
            val snapshot = query.get().await()
            val shops = snapshot.documents.mapNotNull {
                it.toObject(CollectionHistory::class.java)
                    ?.apply {
                        id = it.id
                    }
            }
            val nextQuery = if (snapshot.documents.isNotEmpty()) {
                firestore.collection(TRANSECTION_HISTORY_COLLECTION)
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

    override fun getRefreshKey(state: PagingState<Query, CollectionHistory>): Query? = null
}