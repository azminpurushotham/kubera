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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

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
    val pageSize = 1 // Number of documents per page
    private var lastDocumentSnapshot: DocumentSnapshot? =
        null // Store the last document of the current page
    private val baseQuery = firestore.collection(SHOP_COLLECTION)
    val shopFlow = Pager(
        PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { ShopListPagingSource(baseQuery) }
    ).flow.cachedIn(viewModelScope)

    fun init() {
//        getBalance()
        getTodaysCollectionLogic()
    }

    fun onResume() {
//        getBalance()
        getTodaysCollectionLogic()
    }

    fun onRefresh() {
        getSwipeShops()
//        getBalance()
        getTodaysCollectionLogic()
    }

    private fun getTodaysCollectionLogic() {
        clearTodaysCollectionLogic()
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
                        _balance.value = it[0].balance
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

    private fun clearTodaysCollectionLogic() {
        Timber.v("clearTodaysCollectionLogic")
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
                        if (it.isNotEmpty()) {
                            val cd = formatFirestoreTimestamp(it[0].timestamp)
                            val n = formatFirestoreTimestamp(Timestamp.now())
                            if (cd != n) {
                                clearPreviousDaysCollections()
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

    private fun clearPreviousDaysCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TODAYS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = firestore.batch()
                    for (document in querySnapshot.documents) {
                        batch.delete(document.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            Timber.tag("clearTodaysCollectionLogic")
                                .i("Collection deleted successfully")
                        }
                        .addOnFailureListener { e ->
                            Timber.tag("clearTodaysCollectionLogic")
                                .i("Error deleting collection: $e")
                        }
                }
                .addOnFailureListener { e ->
                    Timber.tag("clearTodaysCollectionLogic")
                        .i("Error fetching collection: $e")
                }
        }
    }


    private fun getSwipeShops() {
        Timber.v("getSwipeShops")
        _uiState.value = HomeUiState.Refreshing
        lastDocumentSnapshot = null
        viewModelScope.launch(Dispatchers.IO) {
            var query = firestore.collection(SHOP_COLLECTION)
                .limit(pageSize.toLong())
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
                .limit(pageSize.toLong())
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
                    .limit(10)
                    .get()
                val q2 = firestore.collection(SHOP_COLLECTION)
//                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
//                    .whereEqualTo("s_firstName", listOf( shopName.lowercase()))
                    .limit(10)
                    .get()
                val q3 = firestore.collection(SHOP_COLLECTION)
                    .whereGreaterThanOrEqualTo("s_lastName", shopName.lowercase())
                    .whereLessThan("s_lastName", shopName.lowercase())
//                    .whereEqualTo("s_lastName", listOf( shopName.lowercase()))
                    .limit(10)
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
        }
    }
}

fun formatFirestoreTimestamp(timestamp: Timestamp?): String? {
    if (timestamp == null) return null
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Change format as needed
    val date = timestamp.toDate() // Convert Firestore Timestamp to Date
    return sdf.format(date) // Format Date
}