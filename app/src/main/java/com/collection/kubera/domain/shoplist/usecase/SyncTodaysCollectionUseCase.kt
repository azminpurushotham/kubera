package com.collection.kubera.domain.shoplist.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.TodaysCollectionData
import com.collection.kubera.domain.repository.TodaysCollectionRepository
import javax.inject.Inject

/**
 * Use case for syncing today's collection totals.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class SyncTodaysCollectionUseCase @Inject constructor(
    private val todaysCollectionRepository: TodaysCollectionRepository
) {
    suspend operator fun invoke(): Result<TodaysCollectionData> =
        todaysCollectionRepository.syncTodaysCollection()
}
