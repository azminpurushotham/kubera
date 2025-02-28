package com.collection.kubera.ui.shoporderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.states.HomeUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ShopCollectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _collections = MutableStateFlow<List<CollectionHistory>>(emptyList())
    val collections: StateFlow<List<CollectionHistory>> get() = _collections
    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> get() = _balance
    private val firestore = FirebaseFirestore.getInstance()
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop


    fun getShopDetails(
        id: String
    ) {
        Timber.v("getShopDetails")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(SHOP_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data?.isNotEmpty() == true) {
                        _shop.value = result.toObject(Shop::class.java)
                            ?.apply {
                                this.id = id
                            }
                        _balance.value = shop.value?.balance ?: 0
                    }
                }.addOnFailureListener {e->
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }

    }

    fun getSwipeShopsCollectionHistory() {
        Timber.v("getSwipeShopsCollectionHistory")
        _uiState.value = HomeUiState.Loading
        if ((shop.value?.id?.length ?:0)  > 1) {
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .whereEqualTo("shopId", shop.value?.id)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .get()
                .addOnSuccessListener { snapshot ->
                    _collections.value = snapshot.mapNotNull {
                        it.toObject(CollectionHistory::class.java)
                            .apply {
                                it.data["shopId"]?.let {i->
                                    this.shopId = i.toString()
                                }
                             }
                    }
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
                .addOnFailureListener { e ->
                    Timber.e(e)
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }
    }

    fun getCollectionHistory() {
        Timber.v("getCollectionHistory ${shop ?: "NONE"}")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .whereEqualTo("shopId", shop.value?.id)
                .get()
                .addOnSuccessListener { snapshot ->
                    _collections.value = snapshot.mapNotNull {
                        it.toObject(CollectionHistory::class.java)
                            .apply {
                                it.data["shopId"]?.let {i->
                                    this.shopId = i.toString()
                                }
                              }
                    }
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
                .addOnFailureListener { e ->
                    Timber.e(e)
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }
    }

    fun setShop(shop: Shop) {
        _shop.value = shop
        _balance.value = _shop.value?.balance ?: 0
    }
}