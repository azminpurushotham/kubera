package com.collection.kubera.ui.addnewshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.addnewshop.usecase.AddShopUseCase
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
    private val addShopUseCase: AddShopUseCase,
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
            Timber.d("addShopDetails: calling addShopUseCase")
            when (val result = addShopUseCase(shop)) {
                is Result.Success -> {
                    val addedShop = result.data
                    Timber.d("addShopDetails: addShop Success shopId=${addedShop.id} shopName=${addedShop.shopName}")
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

}
