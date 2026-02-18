package com.collection.kubera.domain.report.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.ReportRepository
import javax.inject.Inject

/**
 * Use case for loading all shops for report generation.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetAllShopsUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(): Result<List<Shop>> = reportRepository.getAllShops()
}
