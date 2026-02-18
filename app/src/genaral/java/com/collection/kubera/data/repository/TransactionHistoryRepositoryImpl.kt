package com.collection.kubera.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.mapper.toDomainCollectionModel
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.repository.TransactionHistoryRepository
import com.collection.kubera.domain.repository.TransactionSortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class TransactionHistoryRepositoryImpl(
    private val collectionHistoryDao: CollectionHistoryDao
) : TransactionHistoryRepository {

    override fun getTransactionHistoryPagingFlow(sortType: TransactionSortType): Flow<PagingData<CollectionModel>> {
        Timber.d("getTransactionHistoryPagingFlow sortType=${sortType.code}")
        val pagingSource = when (sortType) {
            TransactionSortType.TIMESTAMP_ASC -> collectionHistoryDao.getTransactionHistoryAscPagingSource()
            TransactionSortType.TIMESTAMP_DESC -> collectionHistoryDao.getTransactionHistoryPagingSource()
            TransactionSortType.SHOP_NAME_ASC -> collectionHistoryDao.getByShopNameAscPagingSource()
            TransactionSortType.SHOP_NAME_DESC -> collectionHistoryDao.getByShopNameDescPagingSource()
            TransactionSortType.USER_NAME_ASC -> collectionHistoryDao.getByFirstNameAscPagingSource()
            TransactionSortType.USER_NAME_DESC -> collectionHistoryDao.getByFirstNameDescPagingSource()
        }
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.TRANSACTION_HISTORY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { pagingSource }
        ).flow.map { it.map { entity -> entity.toDomainCollectionModel() } }
    }

    override fun getShopCollectionHistoryPagingFlow(shopId: String): Flow<PagingData<CollectionModel>> {
        Timber.d("getShopCollectionHistoryPagingFlow shopId=$shopId")
        return Pager(
            config = PagingConfig(
                pageSize = RepositoryConstants.TRANSACTION_HISTORY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { collectionHistoryDao.getShopCollectionPagingSource(shopId) }
        ).flow.map { it.map { entity -> entity.toDomainCollectionModel() } }
    }
}
