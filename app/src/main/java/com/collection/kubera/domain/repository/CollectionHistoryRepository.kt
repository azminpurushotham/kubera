package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result

interface CollectionHistoryRepository {
    suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit>
}
