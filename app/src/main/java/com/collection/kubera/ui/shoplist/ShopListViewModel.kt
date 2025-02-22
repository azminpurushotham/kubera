package com.collection.kubera.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.collection.kubera.data.BALANCE_COLLECTION
import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TodaysCollections
import com.collection.kubera.states.HomeUiState
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
    private val _balance = MutableStateFlow(0L)
    val balance: StateFlow<Long> get() = _balance
    private val _todaysCollection = MutableStateFlow(0L)
    val todaysCollection: StateFlow<Long> get() = _todaysCollection
    private val _todaysCredit = MutableStateFlow(0L)
    val todaysCredit: StateFlow<Long> get() = _todaysCredit
    private val _todaysDebit = MutableStateFlow(0L)
    val todaysDebit: StateFlow<Long> get() = _todaysDebit
    private val firestore = FirebaseFirestore.getInstance()
    val pageSize = 20L // Number of documents per page
    private var lastDocumentSnapshot: DocumentSnapshot? =
        null // Store the last document of the current page


    val items = Pager(PagingConfig(pageSize = 20)) {
        ShopListPagingSource(pageSize, firestore)
    }.flow.cachedIn(viewModelScope)

    fun getTodaysCollectionLogic() {
        clearTodaysCollection()
    }

    private fun getTodaysCollection() {
        Timber.v("getTodaysBalance")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TODAYS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(TodaysCollections::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }.also {
                        _todaysCollection.value = it[0].balance
                        _todaysCredit.value = it[0].credit
                        _todaysDebit.value = it[0].debit
                    }
                }
                .addOnFailureListener {
                    _todaysCollection.value = 0L
                    _uiState.value = HomeUiState.HomeError(
                        it.message ?: "Something went wrong,Please refresh the page"
                    )
                }
        }
    }

    private fun clearTodaysCollection() {
        Timber.v("clearTodaysCollection")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TODAYS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(TodaysCollections::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }.also {
                        if(it.isNotEmpty()){
                            val cd = it[0].timestamp.toDate()
                            val n = Timestamp.now().toDate()
                            if (cd != n) {
                                firestore.collection(TODAYS_COLLECTION)
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        val batch = firestore.batch()
                                        for (document in querySnapshot.documents) {
                                            batch.delete(document.reference)
                                        }
                                        batch.commit()
                                            .addOnSuccessListener {
                                                Timber.tag("clearTodaysCollection")
                                                    .i("Collection deleted successfully")
                                            }
                                            .addOnFailureListener { e ->
                                                Timber.tag("clearTodaysCollection")
                                                    .i("Error deleting collection: $e")
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Timber.tag("clearTodaysCollection")
                                            .i("Error fetching collection: $e")
                                    }

                            } else {
                                getTodaysCollection()
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    _uiState.value = HomeUiState.HomeError(
                        it.message ?: "Something went wrong,Please refresh the page"
                    )
                }
        }
    }


    fun getShops() {
        Timber.v("getShops")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            var query = firestore.collection(SHOP_COLLECTION)
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
                    lastDocumentSnapshot = if (r.documents.isEmpty()) {
                        null
                    } else {
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
            var query = firestore.collection(SHOP_COLLECTION)
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
                    lastDocumentSnapshot = if (r.documents.isEmpty()) {
                        null
                    } else {
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
            var query = firestore.collection(SHOP_COLLECTION)
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
                    lastDocumentSnapshot = if (r.documents.isEmpty()) {
                        null
                    } else {
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
                val q1 = firestore.collection(SHOP_COLLECTION)
                    .whereGreaterThanOrEqualTo("s_shopName", listOf(shopName.lowercase()))
                    .whereLessThan("s_shopName", listOf(shopName.lowercase()))
//                    .whereEqualTo("s_shopName", listOf( shopName.lowercase()))
                    .get()
                val q2 = firestore.collection(SHOP_COLLECTION)
//                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
//                    .whereEqualTo("s_firstName", listOf( shopName.lowercase()))
                    .get()
                val q3 = firestore.collection(SHOP_COLLECTION)
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
                        }.distinct()
                        _shopList.value = results.mapNotNull {
                            it.toObject(Shop::class.java)
                                ?.apply {
                                    id = it.id
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

    internal fun getBalance() {
        Timber.tag("getBalance").i("getBalance")
        viewModelScope.launch(Dispatchers.IO) {
            async {
                firestore.collection(BALANCE_COLLECTION)
                    .get()
                    .addOnSuccessListener {
                        val balanceAmounts = it.documents.mapNotNull { item ->
                            item.toObject(BalanceAmount::class.java)
                                ?.apply {
                                    id = item.id
                                }
                        }
                        if (balanceAmounts.isNotEmpty() && balanceAmounts[0].balance > 0) {
                            Timber.tag("getBalance").i(it.toString())
                            _balance.value = balanceAmounts[0].balance
                        } else {
                            _balance.value = 0L
                        }
                    }
                    .addOnFailureListener {
                        Timber.e(it)
                        _balance.value = 0L
                        _uiState.value =
                            HomeUiState.HomeError(it.message ?: "Unable to show balance")
                    }
            }.await()
        }
    }


}