package com.collection.kubera.ui.shopdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.BalanceRepository
import com.collection.kubera.data.repository.CollectionHistoryRepository
import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.data.repository.UserPreferencesRepository
import com.collection.kubera.states.ShopDetailUiState
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShopDetailsViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val collectionHistoryRepository: CollectionHistoryRepository,
    private val balanceRepository: BalanceRepository,
    private val todaysCollectionRepository: TodaysCollectionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShopDetailUiState>(ShopDetailUiState.Initial)
    val uiState: Flow<ShopDetailUiState> = _uiState.distinctUntilChanged { a, b -> a == b }

    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopDetailsUiEvent>()
    val uiEvent: SharedFlow<ShopDetailsUiEvent> = _uiEvent.asSharedFlow()

    private fun updateState(newState: ShopDetailUiState) {
        if (_uiState.value != newState) {
            _uiState.value = newState
        }
    }

    fun setShop(model: Shop) {
        _shop.value = model
        Timber.tag("setShop").i(model.toString())
    }

    fun getShopDetails(id: String) {
        Timber.i("getShopDetails")
        viewModelScope.launch(dispatcher) {
            when (val result = shopRepository.getShopById(id)) {
                is Result.Success -> {
                    _shop.value = result.data?.apply { this.id = id }
                    updateState(ShopDetailUiState.ShopDetailSuccess("Success"))
                }
                is Result.Error -> {
                    updateState(ShopDetailUiState.ShopDetailError("Error"))
                }
            }
        }
    }

    fun updateBalance(id: String?, amountStr: String, selectedOption: String) {
        Timber.tag("updateBalance").i("id -> $id b -> $amountStr selectedOption -> $selectedOption")
        val shopValue = _shop.value ?: return
        id ?: return

        val amount = amountStr.toLongOrNull() ?: return
        val newBalance = if (selectedOption == "Credit") {
            (shopValue.balance ?: 0L) + amount
        } else {
            (shopValue.balance ?: 0L) - amount
        }

        viewModelScope.launch(dispatcher) {
            updateState(ShopDetailUiState.Loading)
            when (shopRepository.updateShopBalance(id, newBalance)) {
                is Result.Success -> {
                    _shop.value = shopValue.copy(balance = newBalance)
                    val collectionModel = buildCollectionModel(shopValue, amount, selectedOption)
                    when (collectionHistoryRepository.insertCollectionHistory(collectionModel)) {
                        is Result.Success -> Timber.tag("insertCollectionHistory").i("Success")
                        is Result.Error -> Timber.e("insertCollectionHistory failed")
                    }
                    val isCredit = selectedOption == "Credit"
                    when (balanceRepository.updateBalance(amount, isCredit)) {
                        is Result.Success -> Timber.tag("updateTotalBalance").i("Success")
                        is Result.Error -> Timber.e("updateTotalBalance failed")
                    }
                    when (todaysCollectionRepository.updateOrInsertTodaysCollection(amount, isCredit)) {
                        is Result.Success -> Timber.tag("updateTodaysCollection").i("Success")
                        is Result.Error -> Timber.e("updateTodaysCollection failed")
                    }
                    val message = "Successfully balance updated"
                    updateState(ShopDetailUiState.ShopDetailSuccess("Success"))
                    _uiEvent.emit(ShopDetailsUiEvent.ShowToast(message))
                    _uiEvent.emit(ShopDetailsUiEvent.PopBack(message))
                }
                is Result.Error -> {
                    updateState(ShopDetailUiState.ShopDetailError("Balance not updated"))
                    _uiEvent.emit(ShopDetailsUiEvent.ShowToast("Balance not updated"))
                }
            }
        }
    }

    private fun buildCollectionModel(shop: Shop, amount: Long, selectedOption: String): CollectionModel {
        val balanceAmount = if (selectedOption == "Credit") amount else -amount
        return CollectionModel().apply {
            if (shop.id?.isNotEmpty() == true) shopId = shop.id
            if (shop.shopName.isNotEmpty()) {
                shopName = shop.shopName
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
