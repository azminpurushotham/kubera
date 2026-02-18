package com.collection.kubera.domain.report.usecase

import com.collection.kubera.data.Result
import com.collection.kubera.data.repository.BalanceRepository
import javax.inject.Inject

/**
 * Use case for loading current balance.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetBalanceUseCase @Inject constructor(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(): Result<Long> = balanceRepository.getBalance()
}
