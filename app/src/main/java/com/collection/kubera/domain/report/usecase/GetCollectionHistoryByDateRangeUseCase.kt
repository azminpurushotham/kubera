package com.collection.kubera.domain.report.usecase

import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.ReportRepository
import javax.inject.Inject

/**
 * Use case for loading collection history by date range.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetCollectionHistoryByDateRangeUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(
        startTimestampMillis: Long,
        endTimestampMillis: Long
    ): Result<List<CollectionModel>> =
        reportRepository.getCollectionHistoryByDateRange(startTimestampMillis, endTimestampMillis)
}
