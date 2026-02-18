package com.collection.kubera.domain.report.usecase

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.repository.ReportRepository
import com.google.firebase.Timestamp
import javax.inject.Inject

/**
 * Use case for loading collection history by date range.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetCollectionHistoryByDateRangeUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): Result<List<CollectionModel>> =
        reportRepository.getCollectionHistoryByDateRange(startTimestamp, endTimestamp)
}
