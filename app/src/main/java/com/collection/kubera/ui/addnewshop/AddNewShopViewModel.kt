package com.collection.kubera.ui.addnewshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.BalanceRepository
import com.collection.kubera.data.repository.CollectionHistoryRepository
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.data.repository.UserPreferencesRepository
import com.collection.kubera.states.AddNewShopUiState
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddNewShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val collectionHistoryRepository: CollectionHistoryRepository,
    private val balanceRepository: BalanceRepository,
    private val todaysCollectionRepository: TodaysCollectionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddNewShopUiState>(AddNewShopUiState.Initial)
    val uiState: StateFlow<AddNewShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddNewShopUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<AddNewShopUiEvent> = _uiEvent.asSharedFlow()

    fun addShopDetails(
        shopName: String,
        location: String,
        landmark: String?,
        balance: String?,
        firstName: String,
        lastName: String?,
        phoneNumber: String,
        secondPhoneNumber: String?,
        mailId: String?
    ) {
        Timber.d("addShopDetails")
        _uiState.value = AddNewShopUiState.Loading

        val shop = Shop().apply {
            if (shopName.isNotEmpty()) {
                this.shopName = shopName
                this.s_shopName = shopName.lowercase()
            }
            if (location.isNotEmpty()) this.location = location
            if ((landmark ?: "").isNotEmpty()) this.landmark = landmark!!
            if ((balance ?: "0").isNotEmpty()) this.balance = (balance ?: "0").toLong()
            if (firstName.isNotEmpty()) {
                this.firstName = firstName
                this.s_firstName = firstName.lowercase()
            }
            if ((lastName ?: "").isNotEmpty()) {
                this.lastName = lastName!!
                this.s_lastName = (lastName ?: "").lowercase()
            }
            if (phoneNumber.isNotEmpty()) this.phoneNumber = phoneNumber
            if (secondPhoneNumber != null && secondPhoneNumber.isNotEmpty()) {
                this.secondPhoneNumber = secondPhoneNumber
            }
            if ((mailId ?: "").isNotEmpty()) this.mailId = mailId!!
            this.timestamp = Timestamp.now()
            this.status = true
        }

        viewModelScope.launch(dispatcher) {
            Timber.d("addShopDetails: calling shopRepository.addShop")
            when (val result = shopRepository.addShop(shop)) {
                is Result.Success -> {
                    val addedShop = result.data
                    Timber.d("addShopDetails: addShop Success shopId=${addedShop.id} shopName=${addedShop.shopName}")
                    if ((addedShop.balance ?: 0L) != 0L) {
                        insertCollectionHistory(addedShop)
                        balanceRepository.updateBalance(addedShop.balance!!, true)
                        todaysCollectionRepository.updateOrInsertTodaysCollection(
                            addedShop.balance!!,
                            true
                        )
                    }
                    Timber.d("addShopDetails: emitting ShowSuccess and NavigateBack")
                    _uiEvent.emit(AddNewShopUiEvent.ShowSuccess(RepositoryConstants.ADD_SHOP_SUCCESS_MESSAGE))
                    _uiEvent.emit(AddNewShopUiEvent.NavigateBack)
                }
                is Result.Error -> {
                    Timber.e(result.exception, "addShopDetails: addShop Error")
                    _uiEvent.emit(
                        AddNewShopUiEvent.ShowError(
                            result.exception.message ?: RepositoryConstants.ADD_SHOP_ERROR_MESSAGE
                        )
                    )
                }
            }
            _uiState.value = AddNewShopUiState.Initial
            Timber.d("addShopDetails: completed")
        }
    }

    private suspend fun insertCollectionHistory(shop: Shop) {
        Timber.d("insertCollectionHistory")
        val collectionModel = CollectionModel().apply {
            if (shop.id.isNotEmpty()) this.shopId = shop.id
            if (shop.shopName.isNotEmpty()) {
                this.shopName = shop.shopName
                this.s_shopName = shop.shopName.lowercase()
            }
            if ((shop.balance ?: 0L) != 0L) this.amount = shop.balance
            if (shop.firstName.isNotEmpty()) {
                this.firstName = shop.firstName
                this.s_firstName = shop.firstName.lowercase()
            }
            if ((shop.lastName ?: "").isNotEmpty()) {
                this.lastName = shop.lastName
                this.s_lastName = (shop.lastName ?: "").lowercase()
            }
            if (shop.phoneNumber?.isNotEmpty() == true) this.phoneNumber = shop.phoneNumber
            if (shop.secondPhoneNumber != null && shop.secondPhoneNumber!!.isNotEmpty()) {
                this.secondPhoneNumber = shop.secondPhoneNumber
            }
            if ((shop.mailId ?: "").isNotEmpty()) this.mailId = shop.mailId
            this.collectedById = userPreferencesRepository.getUserId().ifEmpty { null }
            this.collectedBy = userPreferencesRepository.getUserName().ifEmpty { "Admin" }
            this.timestamp = Timestamp.now()
            this.transactionType = when {
                (this.amount ?: 0L) > 0L -> "Credit"
                (this.amount ?: 0L) < 0L -> "Debit"
                else -> null
            }
        }

        when (val result = collectionHistoryRepository.insertCollectionHistory(collectionModel)) {
            is Result.Success -> Timber.d("insertCollectionHistory: success")
            is Result.Error -> {
                Timber.e(result.exception, "insertCollectionHistory: error")
                _uiEvent.emit(
                    AddNewShopUiEvent.ShowError(
                        result.exception.message ?: RepositoryConstants.COLLECTION_HISTORY_NOT_UPDATED
                    )
                )
            }
        }
    }
}
