package com.collection.kubera.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Shop
import com.collection.kubera.data.User
import com.collection.kubera.states.HomeUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class ShopListViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()
    private val _shopList = MutableStateFlow<List<Shop>>(emptyList())
    val shopList: StateFlow<List<Shop>> get() = _shopList
    private val firestore = FirebaseFirestore.getInstance()

    fun getShops() {
        Timber.v("getShops")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("shop")
                .orderBy("shopName", Query.Direction.ASCENDING)
                .get().await()
            _shopList.value = snapshot.documents.mapNotNull {
                it.toObject(Shop::class.java)?.apply { id = it.id }
            }
            _uiState.value = HomeUiState.HomeSuccess("Success")
            _uiState.value = HomeUiState.Initial
        }
    }
}