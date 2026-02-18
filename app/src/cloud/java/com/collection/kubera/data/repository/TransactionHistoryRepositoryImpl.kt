package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.data.mapper.toDomainCollectionModel
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.repository.TransactionHistoryRepository
import com.collection.kubera.domain.repository.TransactionSortType
import com.collection.kubera.utils.FirestoreCollectionPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

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

    override fun getTransactionHistoryPagingFlow(sortType: TransactionSortType): Flow<PagingData<CollectionModel>> {
        Timber.d("getTransactionHistoryPagingFlow sortType=${sortType.code}")
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.TRANSACTION_HISTORY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FirestoreCollectionPagingSource(query = buildQuery(sortType)) }
        ).flow.map { pagingData -> pagingData.map { it.toDomainCollectionModel() } }
    }

    override fun getShopCollectionHistoryPagingFlow(shopId: String): Flow<PagingData<CollectionModel>> {
        Timber.d("getShopCollectionHistoryPagingFlow shopId=$shopId")
        val query = collection
            .whereEqualTo("shopId", shopId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.TRANSACTION_HISTORY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FirestoreCollectionPagingSource(query = query) }
        ).flow.map { pagingData -> pagingData.map { it.toDomainCollectionModel() } }
    }
}
