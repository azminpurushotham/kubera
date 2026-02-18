package com.collection.kubera.domain.orderhistory.usecase

import androidx.paging.PagingData
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.repository.TransactionHistoryRepository
import com.collection.kubera.data.repository.TransactionSortType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for loading transaction/collection history with paging.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetTransactionHistoryPagingUseCase @Inject constructor(
    private val transactionHistoryRepository: TransactionHistoryRepository
) {
    operator fun invoke(sortType: TransactionSortType): Flow<PagingData<CollectionModel>> =
        transactionHistoryRepository.getTransactionHistoryPagingFlow(sortType)
}
