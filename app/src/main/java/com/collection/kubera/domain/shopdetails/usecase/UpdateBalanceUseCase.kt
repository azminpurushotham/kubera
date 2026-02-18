package com.collection.kubera.domain.shopdetails.usecase

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
        return CollectionModel().apply {
            if (shop.id?.isNotEmpty() == true) shopId = shop.id
            if (shop.shopName.isNotEmpty()) {
                this.shopName = shop.shopName
                s_shopName = shop.shopName.lowercase()
            }
            this.amount = balanceAmount
            if (shop.firstName.isNotEmpty()) {
                firstName = shop.firstName
                s_firstName = shop.firstName.lowercase()
            }
            shop.lastName?.let {
                if (it.isNotEmpty()) {
                    lastName = it
                    s_lastName = it.lowercase()
                }
            }
            shop.phoneNumber?.toString()?.takeIf { it.isNotEmpty() }?.let { phoneNumber = it }
            shop.secondPhoneNumber?.toString()?.takeIf { it.isNotEmpty() }?.let { secondPhoneNumber = it }
            collectedBy = userPreferencesRepository.getUserName()
            collectedById = userPreferencesRepository.getUserId()
            shop.mailId?.takeIf { it.isNotEmpty() }?.let { mailId = it }
            timestamp = Timestamp.now()
            transactionType = selectedOption
        }
    }
}
