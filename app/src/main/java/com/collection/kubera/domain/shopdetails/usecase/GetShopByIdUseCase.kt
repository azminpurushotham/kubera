package com.collection.kubera.domain.shopdetails.usecase

import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.ShopRepository
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
