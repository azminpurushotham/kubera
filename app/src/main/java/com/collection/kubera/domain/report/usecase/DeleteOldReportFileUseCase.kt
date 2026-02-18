package com.collection.kubera.domain.report.usecase

import com.collection.kubera.domain.repository.ReportFileHelper
import javax.inject.Inject

/**
 * Use case for deleting old report file. Returns error message if deletion failed, null if successful.
 * Clean Architecture: ViewModel → UseCase → Repository/Helper
 */
class DeleteOldReportFileUseCase @Inject constructor(
    private val reportFileHelper: ReportFileHelper
) {
    operator fun invoke(fileName: String): String? = reportFileHelper.deleteOldFile(fileName)
}
