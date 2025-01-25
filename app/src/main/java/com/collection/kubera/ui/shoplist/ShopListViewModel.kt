package com.collection.kubera.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ShopListViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _shopList = MutableStateFlow<List<Shop>>(emptyList())
    val shopList: StateFlow<List<Shop>> get() = _shopList
    private val _balance = MutableStateFlow<Double>(0.0)
    val balance: StateFlow<Double> get() = _balance
    private val firestore = FirebaseFirestore.getInstance()

    fun getShops() {
        Timber.v("getShops")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("shop")
                .orderBy("shopName", Query.Direction.ASCENDING)
                .get().await()
            _shopList.value = snapshot.documents.mapNotNull {
                it.toObject(Shop::class.java)
                    ?.apply {
                        id = it.id
                    }
            }
            _uiState.value = HomeUiState.HomeSuccess("Success")
        }
    }

    fun getBalance() {
        Timber.v("getBalance")
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
                    _balance.value = total
                }
                .addOnFailureListener {
                    _balance.value = 0.0
                }
        }
    }

    fun getSwipeShops() {
        Timber.v("getShops")
        _uiState.value = HomeUiState.Refreshing
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("shop")
                .orderBy("shopName", Query.Direction.ASCENDING)
                .get().await()
            _shopList.value = snapshot.documents.mapNotNull {
                it.toObject(Shop::class.java)
                    ?.apply {
                        id = it.id
                    }
            }
            _uiState.value = HomeUiState.HomeSuccess("Success")
        }
    }

    fun getShops(shopName: String) {
        Timber.v("getShops ${shopName}")
        if (shopName.length > 1) {
            _uiState.value = HomeUiState.Searching
            viewModelScope.launch(Dispatchers.IO) {
                val q1 = firestore.collection("shop")
                    .whereGreaterThanOrEqualTo("s_shopName", listOf(shopName.lowercase()))
                    .whereLessThan("s_shopName", listOf(shopName.lowercase()))
//                    .whereEqualTo("s_shopName", listOf( shopName.lowercase()))
                    .get()
                val q2 = firestore.collection("shop")
//                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
//                    .whereEqualTo("s_firstName", listOf( shopName.lowercase()))
                    .get()
                val q3 = firestore.collection("shop")
                    .whereGreaterThanOrEqualTo("s_lastName", shopName.lowercase())
                    .whereLessThan("s_lastName", shopName.lowercase())
//                    .whereEqualTo("s_lastName", listOf( shopName.lowercase()))
                    .get()


                Tasks.whenAllComplete(/*q1, */q2/*, q3*/)
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
                            it.toObject(Shop::class.java)
                                ?.apply {
                                    id = it.id
                                }
                        }
//                        _shopList.value = combinedResults.mapNotNull {
//                            Gson().fromJson(it.toString(),Shop::class.java)
//                            it.toObject(Shop::class.java)
//                                ?.apply {
//                                    id = it.id
//                                }
//                        }
                    }
                    .addOnFailureListener { e ->
                        println("Error querying documents: $e")
                    }

                /* firestore.collection("shop")
                     .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
                     .whereGreaterThanOrEqualTo("s_lastName", shopName.lowercase())
                     .whereGreaterThanOrEqualTo("s_shopName", shopName.lowercase())
 //                    .whereArrayContains("s_firstname", shopName)
 //                    .whereLessThanOrEqualTo("s_firstname", shopName)
 //                    .whereLessThan(
 //                        "s_firstname",
 //                        shopName + "\uf8ff"
 //                    ) // "\uf8ff" ensures the range ends after the prefix
                     .get()
                     .addOnSuccessListener { result ->
                         _shopList.value = result.documents.mapNotNull {
                             it.toObject(Shop::class.java)
                                 ?.apply {
                                     id = it.id
                                 }
                         }
                     }
                     .addOnFailureListener { e ->
                         println("Error querying documents: $e")
                     }*/
                _uiState.value = HomeUiState.HomeSuccess("Success")
            }
        }
    }
}