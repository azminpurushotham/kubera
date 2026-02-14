package com.collection.kubera.ui.updateshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.states.UpdateShopUiState
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
class UpdateShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateShopUiState>(UpdateShopUiState.Initial)
    val uiState: StateFlow<UpdateShopUiState> = _uiState.asStateFlow()

    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UpdateShopUiEvent>()
    val uiEvent: SharedFlow<UpdateShopUiEvent> = _uiEvent.asSharedFlow()

    fun init(shop: Shop) {
        Timber.d("init shop=${shop.id}")
        _shop.value = shop
    }

    fun saveShopDetails(
        shopName: String?,
        location: String?,
        landmark: String?,
        balance: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        secondPhoneNumber: String?,
        mailId: String?
    ) {
        Timber.d("saveShopDetails")
        val currentShop = _shop.value ?: return

        val updates = mutableMapOf<String, Any>()
        if ((shopName ?: "").isNotEmpty()) {
            updates["shopName"] = shopName!!
            updates["s_shopName"] = shopName.lowercase()
        }
        if ((firstName ?: "").isNotEmpty()) {
            updates["firstName"] = firstName!!
            updates["s_firstName"] = firstName.lowercase()
        }
        if ((lastName ?: "").isNotEmpty()) {
            updates["lastName"] = lastName!!
            updates["s_lastName"] = lastName.lowercase()
        }
        if ((balance ?: "").isNotEmpty()) {
            updates["balance"] = balance!!.toLong()
        }
        if ((location ?: "").isNotEmpty()) {
            updates["location"] = location!!
        }
        if ((landmark ?: "").isNotEmpty()) {
            updates["landmark"] = landmark!!
        }
        if ((phoneNumber ?: "").isNotEmpty()) {
            updates["phoneNumber"] = phoneNumber!!
        }
        if ((secondPhoneNumber ?: "").isNotEmpty()) {
            updates["secondPhoneNumber"] = secondPhoneNumber!!
        }
        if ((mailId ?: "").isNotEmpty()) {
            updates["mailId"] = mailId!!
        }
        updates["timestamp"] = Timestamp.now()
        updates["status"] = currentShop.status

        _uiState.value = UpdateShopUiState.Loading
        viewModelScope.launch(dispatcher) {
            when (val result = shopRepository.updateShop(currentShop.id, updates)) {
                is Result.Success -> {
                    _uiState.value = UpdateShopUiState.Initial
                    _uiEvent.tryEmit(
                        UpdateShopUiEvent.ShowSuccess(RepositoryConstants.UPDATE_SHOP_SUCCESS_MESSAGE)
                    )
                    _uiEvent.tryEmit(UpdateShopUiEvent.NavigateBack)
                }
                is Result.Error -> {
                    _uiState.value = UpdateShopUiState.Initial
                    _uiEvent.tryEmit(
                        UpdateShopUiEvent.ShowError(
                            result.exception.message
                                ?: RepositoryConstants.UPDATE_SHOP_ERROR_MESSAGE
                        )
                    )
                }
            }
        }
    }
}
