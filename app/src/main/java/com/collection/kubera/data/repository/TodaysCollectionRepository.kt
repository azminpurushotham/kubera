package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.TodaysCollectionData

interface TodaysCollectionRepository {
    suspend fun getTodaysCollection(): Result<TodaysCollectionData>
    suspend fun syncTodaysCollection(): Result<TodaysCollectionData>
    suspend fun updateOrInsertTodaysCollection(delta: Long, isCredit: Boolean): Result<Unit>
}
