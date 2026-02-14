package com.collection.kubera.data.repository

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result

interface CollectionHistoryRepository {
    suspend fun insertCollectionHistory(model: CollectionModel): Result<Unit>
}
