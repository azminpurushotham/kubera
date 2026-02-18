package com.collection.kubera.domain.addnewshop.usecase

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
 * Use case for adding a new shop and associated collection/balance updates.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class AddShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
    private val collectionHistoryRepository: CollectionHistoryRepository,
    private val balanceRepository: BalanceRepository,
    private val todaysCollectionRepository: TodaysCollectionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(shop: Shop): Result<Shop> {
        val addResult = shopRepository.addShop(shop)
        if (addResult is Result.Success) {
            val addedShop = addResult.data
            if ((addedShop.balance ?: 0L) != 0L) {
                val collectionModel = buildCollectionModel(addedShop)
                collectionHistoryRepository.insertCollectionHistory(collectionModel)
                balanceRepository.updateBalance(addedShop.balance!!, true)
                todaysCollectionRepository.updateOrInsertTodaysCollection(addedShop.balance!!, true)
            }
        }
        return addResult
    }

    private fun buildCollectionModel(shop: Shop): CollectionModel {
        val amount = shop.balance ?: 0L
        val transactionType = when {
            amount > 0L -> "Credit"
            amount < 0L -> "Debit"
            else -> null
        }
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
            amount = amount.takeIf { it != 0L },
            collectedBy = userPreferencesRepository.getUserName().ifEmpty { "Admin" },
            collectedById = userPreferencesRepository.getUserId().ifEmpty { null },
            transactionType = transactionType,
            timestampMillis = System.currentTimeMillis(),
        )
    }
}
