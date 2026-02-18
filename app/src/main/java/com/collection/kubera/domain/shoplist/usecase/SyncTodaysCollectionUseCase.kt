package com.collection.kubera.domain.shoplist.usecase

import com.collection.kubera.data.Result
import com.collection.kubera.data.TodaysCollectionData
import com.collection.kubera.data.repository.TodaysCollectionRepository
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
