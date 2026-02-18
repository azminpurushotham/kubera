package com.collection.kubera.domain.shopdetails.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.ShopRepository
import javax.inject.Inject

/**
 * Use case for loading shop details by ID.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetShopByIdUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(id: String): Result<Shop?> = shopRepository.getShopById(id)
}
