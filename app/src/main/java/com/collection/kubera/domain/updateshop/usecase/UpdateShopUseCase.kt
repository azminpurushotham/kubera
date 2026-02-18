package com.collection.kubera.domain.updateshop.usecase

import com.collection.kubera.data.Result
import com.collection.kubera.data.repository.ShopRepository
import javax.inject.Inject

/**
 * Use case for updating shop details.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class UpdateShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shopId: String, updates: Map<String, Any>): Result<Unit> =
        shopRepository.updateShop(shopId, updates)
}
