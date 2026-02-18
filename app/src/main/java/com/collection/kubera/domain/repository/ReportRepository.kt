package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop

interface ReportRepository {
    suspend fun getCollectionHistoryByDateRange(
        startTimestampMillis: Long,
        endTimestampMillis: Long
    ): Result<List<CollectionModel>>

    suspend fun getAllShops(): Result<List<Shop>>
}
