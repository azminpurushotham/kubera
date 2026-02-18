package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.CollectionModel
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData

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
    fun getTransactionHistoryPagingFlow(sortType: TransactionSortType): Flow<PagingData<CollectionModel>>
    fun getShopCollectionHistoryPagingFlow(shopId: String): Flow<PagingData<CollectionModel>>
}
