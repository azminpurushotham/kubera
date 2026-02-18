package com.collection.kubera.domain.shoplist.usecase

import androidx.paging.PagingData
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching shops by name with paging.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class SearchShopsUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Shop>> =
        shopRepository.getShopsSearchPagingFlow(query.trim())
}
