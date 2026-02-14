package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.utils.FirestorePagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

enum class TransactionSortType(val code: String) {
    TIMESTAMP_DESC("DESC"),
    TIMESTAMP_ASC("ASC"),
    SHOP_NAME_ASC("SAZ"),
    SHOP_NAME_DESC("SZA"),
    USER_NAME_ASC("UAZ"),
    USER_NAME_DESC("UZA");

    companion object {
        fun fromCode(code: String?): TransactionSortType =
            entries.find { it.code == code } ?: TIMESTAMP_DESC
    }
}

interface TransactionHistoryRepository {
    fun getTransactionHistoryPagingFlow(sortType: TransactionSortType): Flow<PagingData<DocumentSnapshot>>
}

class TransactionHistoryRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TransactionHistoryRepository {

    private val collection
        get() = firestore.collection(TRANSECTION_HISTORY_COLLECTION)

    private fun buildQuery(sortType: TransactionSortType): Query =
        when (sortType) {
            TransactionSortType.TIMESTAMP_ASC ->
                collection.orderBy("timestamp", Query.Direction.ASCENDING)
            TransactionSortType.TIMESTAMP_DESC ->
                collection.orderBy("timestamp", Query.Direction.DESCENDING)
            TransactionSortType.SHOP_NAME_ASC ->
                collection.orderBy("s_shopName", Query.Direction.ASCENDING)
            TransactionSortType.SHOP_NAME_DESC ->
                collection.orderBy("s_shopName", Query.Direction.DESCENDING)
            TransactionSortType.USER_NAME_ASC ->
                collection.orderBy("s_firstName", Query.Direction.ASCENDING)
            TransactionSortType.USER_NAME_DESC ->
                collection.orderBy("s_firstName", Query.Direction.DESCENDING)
        }

    private fun createPager(sortType: TransactionSortType): Pager<Query, DocumentSnapshot> =
        Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.TRANSACTION_HISTORY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FirestorePagingSource(query = buildQuery(sortType))
            }
        )

    override fun getTransactionHistoryPagingFlow(sortType: TransactionSortType): Flow<PagingData<DocumentSnapshot>> {
        Timber.d("getTransactionHistoryPagingFlow sortType=${sortType.code}")
        return createPager(sortType).flow
    }
}
