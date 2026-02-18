package com.collection.kubera.domain.shopdetails.usecase

import com.collection.kubera.domain.model.CollectionModel
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.Shop
import com.collection.kubera.domain.repository.BalanceRepository
import com.collection.kubera.domain.repository.CollectionHistoryRepository
import com.collection.kubera.domain.repository.ShopRepository
import com.collection.kubera.domain.repository.TodaysCollectionRepository
import com.collection.kubera.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use case for updating shop balance and related totals.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class UpdateBalanceUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
    private val collectionHistoryRepository: CollectionHistoryRepository,
    private val balanceRepository: BalanceRepository,
    private val todaysCollectionRepository: TodaysCollectionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(
        shop: Shop,
        shopId: String,
        newBalance: Long,
        amount: Long,
        selectedOption: String
    ): Result<Unit> {
        return when (val updateResult = shopRepository.updateShopBalance(shopId, newBalance)) {
            is Result.Success -> {
                val collectionModel = buildCollectionModel(shop, amount, selectedOption)
                collectionHistoryRepository.insertCollectionHistory(collectionModel)
                val isCredit = selectedOption == "Credit"
                balanceRepository.updateBalance(amount, isCredit)
                todaysCollectionRepository.updateOrInsertTodaysCollection(amount, isCredit)
                Result.Success(Unit)
            }
            is Result.Error -> updateResult
        }
    }

    private fun buildCollectionModel(shop: Shop, amount: Long, selectedOption: String): CollectionModel {
        val balanceAmount = if (selectedOption == "Credit") amount else -amount
        return CollectionModel(
            shopId = shop.id.takeIf { it.isNotEmpty() },
            shopName = shop.shopName.takeIf { it.isNotEmpty() },
            sShopName = shop.shopName.takeIf { it.isNotEmpty() }?.lowercase(),
            firstName = shop.firstName.takeIf { it.isNotEmpty() },
            sFirstName = shop.firstName.takeIf { it.isNotEmpty() }?.lowercase(),
            lastName = shop.lastName.takeIf { it.isNotEmpty() },
            sLastName = shop.lastName.takeIf { it.isNotEmpty() }?.lowercase(),
            phoneNumber = shop.phoneNumber,
            secondPhoneNumber = shop.secondPhoneNumber,
            mailId = shop.mailId.takeIf { it.isNotEmpty() },
            amount = balanceAmount,
            collectedBy = userPreferencesRepository.getUserName().ifEmpty { "Admin" },
            collectedById = userPreferencesRepository.getUserId().ifEmpty { null },
            transactionType = selectedOption,
            timestampMillis = System.currentTimeMillis(),
        )
    }
}
