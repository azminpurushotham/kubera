package com.collection.kubera.domain.addnewshop.usecase

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.BalanceRepository
import com.collection.kubera.data.repository.CollectionHistoryRepository
import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.data.repository.UserPreferencesRepository
import com.google.firebase.Timestamp
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

    private fun buildCollectionModel(shop: Shop): CollectionModel = CollectionModel().apply {
        if (shop.id.isNotEmpty()) shopId = shop.id
        if (shop.shopName.isNotEmpty()) {
            this.shopName = shop.shopName
            s_shopName = shop.shopName.lowercase()
        }
        if ((shop.balance ?: 0L) != 0L) amount = shop.balance
        if (shop.firstName.isNotEmpty()) {
            firstName = shop.firstName
            s_firstName = shop.firstName.lowercase()
        }
        if ((shop.lastName ?: "").isNotEmpty()) {
            lastName = shop.lastName
            s_lastName = (shop.lastName ?: "").lowercase()
        }
        if (shop.phoneNumber?.isNotEmpty() == true) phoneNumber = shop.phoneNumber
        shop.secondPhoneNumber?.takeIf { it.isNotEmpty() }?.let { secondPhoneNumber = it }
        if ((shop.mailId ?: "").isNotEmpty()) mailId = shop.mailId
        collectedById = userPreferencesRepository.getUserId().ifEmpty { null }
        collectedBy = userPreferencesRepository.getUserName().ifEmpty { "Admin" }
        timestamp = Timestamp.now()
        transactionType = when {
            (amount ?: 0L) > 0L -> "Credit"
            (amount ?: 0L) < 0L -> "Debit"
            else -> null
        }
    }
}
