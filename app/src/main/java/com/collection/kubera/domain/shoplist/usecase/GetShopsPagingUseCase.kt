package com.collection.kubera.domain.shoplist.usecase

import androidx.paging.PagingData
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for loading the shops list with paging.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetShopsPagingUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<PagingData<Shop>> = shopRepository.getShopsPagingFlow()
}
