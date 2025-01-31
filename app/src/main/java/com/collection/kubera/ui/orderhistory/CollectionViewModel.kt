package com.collection.kubera.ui.orderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.Shop
import com.collection.kubera.states.HomeUiState
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class CollectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _shopList = MutableStateFlow<List<CollectionHistory>>(emptyList())
    val shopList: StateFlow<List<CollectionHistory>> get() = _shopList
    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> get() = _balance
    private val firestore = FirebaseFirestore.getInstance()
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop

    fun getCollectionHistory() {
        Timber.v("getCollectionHistory")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("collection_history")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .get()
                .addOnSuccessListener { results ->
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                    _shopList.value = results.mapNotNull {
                        it.toObject(CollectionHistory::class.java)
                            .apply {
                                this.shopId = it.data.get("shopId").toString()
                            }
                    }
                }
                .addOnFailureListener {e->
                    println("Error querying documents: $e")
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }
    }


    internal fun companyBalance() {
        Timber.v("companyBalance")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("shop")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    var total = 0.0
                    val fieldValues = querySnapshot.documents.mapNotNull { it.getDouble("balance") }
                    println("TOTAL")
                    println(fieldValues)
                    fieldValues.forEach {
                        total += it
                    }
                    _balance.value = total.toLong()
                }
                .addOnFailureListener {
                    _balance.value = 0
                }
        }
    }

    fun getSwipeShopsCollectionHistory() {
        Timber.v("getSwipeShopsCollectionHistory")
        _uiState.value = HomeUiState.Refreshing
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("collection_history")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .get()
                .addOnSuccessListener { results ->
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                    _shopList.value = results.mapNotNull {
                        it.toObject(CollectionHistory::class.java)
                            .apply {
                                this.shopId = it.data.get("shopId").toString()
                            }
                    }
                }
                .addOnFailureListener {e->
                    println("Error querying documents: $e")
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }
    }
}