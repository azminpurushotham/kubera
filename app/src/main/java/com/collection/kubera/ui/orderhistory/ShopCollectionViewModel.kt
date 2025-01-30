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

class ShopCollectionViewModel : ViewModel() {
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
            val snapshot = firestore.collection("collection_history")
                .orderBy("shopName", Query.Direction.ASCENDING)
                .get().await()
            _shopList.value = snapshot.documents.mapNotNull {
                it.toObject(CollectionHistory::class.java)
                    ?.apply {
                        id = it.id
                    }
            }
            _uiState.value = HomeUiState.HomeSuccess("Success")
        }
    }

    fun getBalance(id: String?) {
        Timber.v("getBalance")
        if (id != null) {
            getShopDetails(id)
        }else{
            companyBalance()
        }
    }

    private fun companyBalance() {
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
                        _balance.value = shop.value?.balance?:0
                    }
                }.addOnFailureListener {
                }
        }

    }

    fun getSwipeShopsCollectionHistory(id: String?) {
        Timber.v("getShops")
        if (id != null) {
            _uiState.value = HomeUiState.Refreshing
            if (id.length > 1) {
                _uiState.value = HomeUiState.Searching
                viewModelScope.launch(Dispatchers.IO) {
                    val q1 = firestore.collection("collection_history")
                        .whereEqualTo("shopId", id)
                        .get()

                    Tasks.whenAllComplete(q1)
                        .addOnSuccessListener { tasks ->
                            val combinedResults = mutableListOf<Map<String, Any>>()
                            tasks.forEach { snapshot ->
                                (snapshot.result as QuerySnapshot).forEach { document ->
                                    combinedResults.add(document.data ?: emptyMap())
                                    Timber.tag("SEARCH").i("${document.data}")
                                }
                            }
                            val results = tasks.flatMap { task ->
                                val snapshot = task.result as QuerySnapshot
                                snapshot.documents
                            }.distinct() // To remove duplicates if the value matches both fields
                            // Process the results

                            _shopList.value = results.mapNotNull {
                                it.toObject(CollectionHistory::class.java)
                                    ?.apply {
                                        this.shopId = it.data?.get("shopId").toString()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Error querying documents: $e")
                        }

                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
            }
        }
    }

    fun getCollectionHistory(id: String?) {
        Timber.v("getCollectionHistory ${id?:"NONE"}")
        if (id != null) {
            if (id.length > 1) {
                _uiState.value = HomeUiState.Searching
                viewModelScope.launch(Dispatchers.IO) {
                    val q1 = firestore.collection("collection_history")
                        .whereEqualTo("shopId", id)
                        .get()

                    Tasks.whenAllComplete(q1)
                        .addOnSuccessListener { tasks ->
                            val combinedResults = mutableListOf<Map<String, Any>>()
                            tasks.forEach { snapshot ->
                                (snapshot.result as QuerySnapshot).forEach { document ->
                                    combinedResults.add(document.data ?: emptyMap())
                                    Timber.tag("SEARCH").i("${document.data}")
                                }
                            }
                            val results = tasks.flatMap { task ->
                                val snapshot = task.result as QuerySnapshot
                                snapshot.documents
                            }.distinct() // To remove duplicates if the value matches both fields
                            // Process the results

                            _shopList.value = results.mapNotNull {
                                it.toObject(CollectionHistory::class.java)
                                    ?.apply {
                                        this.shopId = it.data?.get("shopId").toString()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Error querying documents: $e")
                        }

                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
            }
        }
    }
}