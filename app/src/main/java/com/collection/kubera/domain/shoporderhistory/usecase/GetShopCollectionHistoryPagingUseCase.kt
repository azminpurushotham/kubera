package com.collection.kubera.domain.shoporderhistory.usecase

import androidx.paging.PagingData
import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.repository.TransactionHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for loading a shop's collection history with paging.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetShopCollectionHistoryPagingUseCase @Inject constructor(
    private val transactionHistoryRepository: TransactionHistoryRepository
) {
    operator fun invoke(shopId: String): Flow<PagingData<CollectionModel>> =
        transactionHistoryRepository.getShopCollectionHistoryPagingFlow(shopId)
}
