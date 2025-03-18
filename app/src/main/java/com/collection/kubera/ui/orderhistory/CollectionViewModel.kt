package com.collection.kubera.ui.orderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.data.TodaysCollections
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.utils.FirestorePagingSource
import com.google.android.gms.tasks.Task
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

class CollectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _shopList = MutableStateFlow<List<CollectionModel>>(emptyList())
    val shopList: StateFlow<List<CollectionModel>> get() = _shopList
    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> get() = _balance
    private val firestore = FirebaseFirestore.getInstance()
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop
    private val _todaysCollection = MutableStateFlow(0L)
    val todaysCollection: StateFlow<Long> get() = _todaysCollection
    private val _todaysCredit = MutableStateFlow(0L)
    val todaysCredit: StateFlow<Long> get() = _todaysCredit
    private val _todaysDebit = MutableStateFlow(0L)
    val todaysDebit: StateFlow<Long> get() = _todaysDebit
    val pageLimit = 15L
    val list: Flow<PagingData<DocumentSnapshot>> = Pager(
        config = PagingConfig(pageSize = pageLimit.toInt(), enablePlaceholders = false),
        pagingSourceFactory = {FirestorePagingSource().also {
            it.setPagination(firestore.collection(TRANSECTION_HISTORY_COLLECTION),pageLimit)
        }}
    ).flow

    fun init() {
//        getCollectionHistory()
//        getBalance()
        getTodaysCollection()
    }

    fun getCollectionHistory(type: String? = null) {
        Timber.v("getCollectionHistory")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val query: Task<QuerySnapshot>
            if (type == "ASC") {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(pageLimit)
                    .get()
            } else if (type == "DESC") {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(pageLimit)
                    .get()
            } else if (type == "SAZ") {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("s_shopName", Query.Direction.ASCENDING)
                    .limit(pageLimit)
                    .get()
            } else if (type == "SZA") {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("s_shopName", Query.Direction.DESCENDING)
                    .limit(pageLimit)
                    .get()
            } else if (type == "UAZ") {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("s_firstName", Query.Direction.ASCENDING)
                    .limit(pageLimit)
                    .get()
            } else if (type == "UZA") {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("s_firstName", Query.Direction.DESCENDING)
                    .limit(pageLimit)
                    .get()
            } else {
                query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(pageLimit)
                    .get()
            }
            query
                .addOnSuccessListener { results ->
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                    _shopList.value = results.mapNotNull {
                        it.toObject(CollectionModel::class.java)
                            .apply {
                                it.data["shopId"]?.let { i ->
                                    shopId = i as String?
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error querying documents: $e")
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }
    }

    fun getSwipeShopsCollectionHistory() {
        Timber.v("getSwipeShopsCollectionHistory")
        _uiState.value = HomeUiState.Refreshing
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .get()
                .addOnSuccessListener { results ->
                    _uiState.value = HomeUiState.HomeSuccess("Success")
                    _shopList.value = results.mapNotNull {
                        it.toObject(CollectionModel::class.java)
                            .apply {
                                it.data["shopId"]?.let { i ->
                                    this.shopId = i.toString()
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error querying documents: $e")
                    _uiState.value = HomeUiState.HomeError("Error querying documents: $e")
                }
        }
    }


    internal fun getTodaysCollection() {
        Timber.v("getTodaysCollection")
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
                            _todaysCollection.value = it[0].balance
                            _todaysCredit.value = it[0].credit
                            _todaysDebit.value = it[0].debit
                        }
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

}