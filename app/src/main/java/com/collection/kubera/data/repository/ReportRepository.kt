package com.collection.kubera.data.repository

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.google.firebase.Timestamp

interface ReportRepository {
    suspend fun getCollectionHistoryByDateRange(
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): Result<List<CollectionModel>>

    suspend fun getAllShops(): Result<List<Shop>>
}
