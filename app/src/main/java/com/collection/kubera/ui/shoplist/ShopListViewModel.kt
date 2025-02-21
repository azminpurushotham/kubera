package com.collection.kubera.ui.shoplist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.collection.kubera.data.Shop
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.utils.getTodayStartAndEndTime
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ShopListViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _shopList = MutableStateFlow<List<Shop>>(emptyList())
    val shopList: StateFlow<List<Shop>> get() = _shopList
    private val _balance = MutableStateFlow<Double>(0.0)
    val balance: StateFlow<Double> get() = _balance
    private val _todaysCollection = MutableStateFlow<Double>(0.0)
    val todaysCollection: StateFlow<Double> get() = _todaysCollection
    private val _todaysCredit = MutableStateFlow(0.0)
    val todaysCredit: StateFlow<Double> get() = _todaysCredit
    private val _todaysDebit = MutableStateFlow(0.0)
    val todaysDebit: StateFlow<Double> get() = _todaysDebit
    private val firestore = FirebaseFirestore.getInstance()
    val pageSize = 1L // Number of documents per page
    var lastDocumentSnapshot: DocumentSnapshot? =  null // Store the last document of the current page


    val items = Pager(PagingConfig(pageSize = 20)) {
        ShopListPagingSource(pageSize,firestore)
    }.flow.cachedIn(viewModelScope)

    fun getBalance() {
        Timber.v("getBalance")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("shop")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    _shopList.value = querySnapshot.documents.mapNotNull { item->
                        item.toObject(Shop::class.java)
                            ?.apply {
                                id = item.id
                            }
                    }
                    var total = 0.0
                    val fieldValues = querySnapshot.documents.mapNotNull { it.getDouble("balance") }
                    println("TOTAL")
                    println(fieldValues)
                    fieldValues.forEach {
                        total += it
                    }
                    _balance.value = total
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
                .addOnFailureListener {
                    _balance.value = 0.0
                    _uiState.value = HomeUiState.HomeError(it.message?:"Something went wrong,Please refresh the page")
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysCollection() {
        Timber.v("getTodaysBalance")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("shop")
                .whereGreaterThanOrEqualTo("timestamp", getTodayStartAndEndTime().first)
                .whereLessThanOrEqualTo("timestamp", getTodayStartAndEndTime().second)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    var total = 0.0
                    val fieldValues = querySnapshot.documents.mapNotNull { it.getDouble("balance") }
                    println("TOTAL")
                    println(fieldValues)
                    fieldValues.forEach {
                        total += it
                    }
                    _todaysCollection.value = total
                }
                .addOnFailureListener {
                    _todaysCollection.value = 0.0
                    _uiState.value = HomeUiState.HomeError(it.message?:"Something went wrong,Please refresh the page")
                }
        }
    }


    fun getShops() {
        Timber.v("getShops")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            var query = firestore.collection("shop")
                .limit(pageSize)
            if (lastDocumentSnapshot != null) {
                query =
                    query.startAfter(lastDocumentSnapshot!!) // Start after the last document of the previous page
            }
            query.get()
                .addOnSuccessListener { r ->
                    _shopList.value = r.documents.mapNotNull {
                        it.toObject(Shop::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }
                    lastDocumentSnapshot = if(r.documents.isEmpty()){
                        null
                    }else{
                        r.documents.last()
                    }
                    Timber.tag("SIZE").i("${shopList.value.size}")
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
                .addOnFailureListener {
                    _uiState.value = HomeUiState.HomeError(it.message ?: "Unknown error")
                }
        }
    }

    fun getSwipeShops() {
        Timber.v("getSwipeShops")
        _uiState.value = HomeUiState.Refreshing
        lastDocumentSnapshot = null
        viewModelScope.launch(Dispatchers.IO) {
            var query = firestore.collection("shop")
                .limit(pageSize)
            if (lastDocumentSnapshot != null) {
                query =
                    query.startAfter(lastDocumentSnapshot!!) // Start after the last document of the previous page
            }
            query.get()
                .addOnSuccessListener { r ->
                    _shopList.value = r.documents.mapNotNull {
                        it.toObject(Shop::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }
                    lastDocumentSnapshot = if(r.documents.isEmpty()){
                        null
                    }else{
                        r.documents.last()
                    }
                    Timber.tag("SIZE").i("${shopList.value.size}")
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
                .addOnFailureListener {
                    _uiState.value = HomeUiState.HomeError(it.message ?: "Unknown error")
                }
        }
    }

    fun getSwipeShopsOnResume() {
        Timber.v("getSwipeShopsOnResume")
        viewModelScope.launch(Dispatchers.IO) {
            var query = firestore.collection("shop")
                .limit(pageSize)
            if (lastDocumentSnapshot != null) {
                query =
                    query.startAfter(lastDocumentSnapshot!!) // Start after the last document of the previous page
            }
            query.get()
                .addOnSuccessListener { r ->
                    _shopList.value = r.documents.mapNotNull {
                        it.toObject(Shop::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }
                    lastDocumentSnapshot = if(r.documents.isEmpty()){
                        null
                    }else{
                        r.documents.last()
                    }
                    Timber.tag("SIZE").i("${shopList.value.size}")
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                }
                .addOnFailureListener {
                    _uiState.value = HomeUiState.HomeError(it.message ?: "Unknown error")
                }
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