package com.collection.kubera.ui.shopdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.Shop
import com.collection.kubera.states.ShopDetailUiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

class ShopDetailsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ShopDetailUiState> =
        MutableStateFlow(ShopDetailUiState.Initial)
    val uiState: Flow<ShopDetailUiState> =
        _uiState.distinctUntilChanged { old, new -> old == new } // Compare only `data` // Ensures only distinct values are emitted
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop
    private val firestore = FirebaseFirestore.getInstance()
    var c = 0

    private fun updateState(newState: ShopDetailUiState) {
        if (_uiState.value != newState) {
            _uiState.value = newState
        }
    }

    fun getShopDetails(
        id: String
    ) {
        Timber.v("getShopDetails")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("shop")
                .document(id)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data?.isNotEmpty() == true) {
                        _shop.value = result.toObject(Shop::class.java)
                            ?.apply {
                                this.id = id
                            }
                    }
                    updateState(ShopDetailUiState.ShopDetailSuccess("Success"))
                }.addOnFailureListener {
                    updateState(ShopDetailUiState.ShopDetailError("Error"))
                }
        }

    }

    fun updateBalance(id: String?, b: String, selectedOption: String) {
        id?.let {
            _uiState.value = ShopDetailUiState.Loading
            val balance = if (selectedOption == "Credit") {
              (shop.value?.balance?:0L) + (b.toLong())
            } else {
                (shop.value?.balance?:0L) - (b.toLong())
            }
            firestore.collection("shop")
                .document(it)
                .update("balance", balance)
                .addOnSuccessListener {
                    insertCollectionHistory(
                        id,
                        b = b,
                        selectedOption = selectedOption
                    )
                    updateState(ShopDetailUiState.ShopDetailToast("Successfully balance updated"))
                    updateState(ShopDetailUiState.ShopDetailsPopBack("Successfully balance updated"))
                }.addOnFailureListener {
                    updateState(ShopDetailUiState.ShopDetailToast("Balance not updated"))
                }
        }
    }

    private fun insertCollectionHistory(id: String?, b: String, selectedOption: String) {
        id?.let {
            val balance = if (selectedOption == "Credit") {
                b
            } else {
                "-${b}"
            }
            viewModelScope.launch(Dispatchers.IO) {
                val shop = shop.value
                val prm = CollectionHistory().apply {
                    if (shop?.id?.isEmpty() != true) {
                        this.shopId = shop?.id!!
                    }
                    if (shop.shopName.isEmpty() != true) this.shopName = shop.shopName
                    if (shop.shopName.isEmpty() != true) this.s_shopName = shop.shopName.lowercase()
                    if ((balance ?: "").isEmpty() != true) this.amount = (balance ?: "0").toLong()
                    if (shop.firstName.isEmpty() != true) this.firstName = shop.firstName
                    if (shop.firstName.isEmpty() != true) this.s_firstName = shop.firstName.lowercase()
                    if ((shop.lastName ?: "").isNotEmpty()) this.lastName = shop.lastName
                    if ((shop.lastName ?: "").isNotEmpty()) this.s_lastName = (shop.lastName ?: "").lowercase()
                    if (shop.phoneNumber.toString().isNotEmpty()) this.phoneNumber = shop.phoneNumber
                    if (shop.secondPhoneNumber != null && secondPhoneNumber.toString()
                            .isNotEmpty()
                    ) this.secondPhoneNumber = secondPhoneNumber!!
                    if ((shop.mailId ?: "").isNotEmpty()) this.mailId = shop.mailId
                    this.timestamp = Timestamp.now()
                    this.transactionType = selectedOption
                }
                firestore.collection("collection_history")
                    .add(prm).addOnSuccessListener {
                        updateState(ShopDetailUiState.ShopDetailToast("Successfully collection history updated"))
                    }.addOnFailureListener {
                        updateState(ShopDetailUiState.ShopDetailToast("Collection history not updated"))
                    }
            }
        }
    }
}