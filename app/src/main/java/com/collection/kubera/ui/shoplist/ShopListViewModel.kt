package com.collection.kubera.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TodaysCollections
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.utils.FirestorePagingSource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    private val _todaysCollection = MutableStateFlow(0L)
    val todaysCollection: StateFlow<Long> get() = _todaysCollection
    private val _todaysCredit = MutableStateFlow(0L)
    val todaysCredit: StateFlow<Long> get() = _todaysCredit
    private val _todaysDebit = MutableStateFlow(0L)
    val todaysDebit: StateFlow<Long> get() = _todaysDebit
    private val firestore = FirebaseFirestore.getInstance()
    val BASE_QUERY by lazy {firestore.collection(SHOP_COLLECTION)}
    private fun createPager(q: Query): Pager<Query, DocumentSnapshot> {
        Timber.tag("createPager").i("MAIN")
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FirestorePagingSource(
                    query = q,
                )
            }
        )
    }

    var list: Flow<PagingData<DocumentSnapshot>> = createPager(BASE_QUERY).flow

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
        Timber.tag("createPager").i("createPager1")
        list = createPager(BASE_QUERY).flow
    }

    fun getSwipeShopsOnResume() {
        Timber.v("getSwipeShopsOnResume")
        viewModelScope.launch(Dispatchers.IO) {
            Timber.tag("createPager").i("createPager2")
            list = createPager(BASE_QUERY).flow
        }
    }

    fun getShops(shopName: String) {
        Timber.v("getShops $shopName")
        if (shopName.length > 1) {
            _uiState.value = HomeUiState.Searching
            viewModelScope.launch(Dispatchers.IO) {
                val q1 = firestore.collection(SHOP_COLLECTION)
                    .whereGreaterThanOrEqualTo("s_shopName", listOf(shopName.lowercase()))
                    .whereLessThan("s_shopName", listOf(shopName.lowercase()))
//                    .whereEqualTo("s_shopName", listOf( shopName.lowercase()))
                val q2 = firestore.collection(SHOP_COLLECTION)
//                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
                    .whereGreaterThanOrEqualTo("s_firstName", shopName.lowercase())
//                    .limit(pageLimit)
//                    .get()
//                    .whereEqualTo("s_firstName", listOf( shopName.lowercase()))
                val q3 = firestore.collection(SHOP_COLLECTION)
                    .whereGreaterThanOrEqualTo("s_lastName", shopName.lowercase())
                    .whereLessThan("s_lastName", shopName.lowercase())
//                    .whereEqualTo("s_lastName", listOf( shopName.lowercase())) 

                Timber.tag("createPager").i("createPager3")
                list = createPager(q2).flow
                _uiState.value = HomeUiState.HomeSuccess("Success")
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