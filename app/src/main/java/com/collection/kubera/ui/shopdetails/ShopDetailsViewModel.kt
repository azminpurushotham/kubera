package com.collection.kubera.ui.shopdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Shop
import com.collection.kubera.data.mapper.toDataShop
import com.collection.kubera.data.mapper.toDomainShop
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.shopdetails.usecase.GetShopByIdUseCase
import com.collection.kubera.domain.shopdetails.usecase.UpdateBalanceUseCase
import com.collection.kubera.states.ShopDetailUiState
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
class ShopDetailsViewModel @Inject constructor(
    private val getShopByIdUseCase: GetShopByIdUseCase,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShopDetailUiState>(ShopDetailUiState.Initial)
    val uiState: StateFlow<ShopDetailUiState> = _uiState.asStateFlow()

    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopDetailsUiEvent>(replay = 0, extraBufferCapacity = 2)
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
            when (val result = getShopByIdUseCase(id)) {
                is Result.Success -> {
                    _shop.value = result.data?.copy(id = id)?.toDataShop()
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
            when (val result = updateBalanceUseCase(shopValue.toDomainShop(), id, newBalance, amount, selectedOption)) {
                is Result.Success -> {
                    _shop.value = shopValue.copy(balance = newBalance)
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

}
