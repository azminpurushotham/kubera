package com.collection.kubera.ui.shopdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Shop
import com.collection.kubera.states.ShopDetailUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ShopDetailsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ShopDetailUiState> =
        MutableStateFlow(ShopDetailUiState.Initial)
    val uiState: StateFlow<ShopDetailUiState> =
        _uiState.asStateFlow()
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop
    private val firestore = FirebaseFirestore.getInstance()

    fun getShopDetails(
        id: String
    ) {
        Timber.v("getShopDetails")
        _uiState.value = ShopDetailUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("shop")
                .document(id)
                .get()
                .addOnSuccessListener {result->
                    if (result.data?.isNotEmpty() == true) {
                        _shop.value =  result.toObject(Shop::class.java)
                    }
                    _uiState.value = ShopDetailUiState.ShopDetailSuccess("Success")
                }.addOnFailureListener {
                    _uiState.value = ShopDetailUiState.ShopDetailError("Error")
                }
        }

    }

    fun updateBalance(id: String?, balance: String) {

    }
}