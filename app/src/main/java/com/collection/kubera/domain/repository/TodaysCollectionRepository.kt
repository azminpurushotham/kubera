package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.TodaysCollectionData

interface TodaysCollectionRepository {
    suspend fun getTodaysCollection(): Result<TodaysCollectionData>
    suspend fun syncTodaysCollection(): Result<TodaysCollectionData>
    suspend fun updateOrInsertTodaysCollection(delta: Long, isCredit: Boolean): Result<Unit>
}
