package com.collection.kubera.ui.orderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.data.TodaysCollections
import com.collection.kubera.states.CollectionHistoryUiState
import com.collection.kubera.utils.FirestorePagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CollectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<CollectionHistoryUiState> =
        MutableStateFlow(CollectionHistoryUiState.Initial)
    val uiState: StateFlow<CollectionHistoryUiState> = _uiState.asStateFlow()
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
    val BASE_QUERY by lazy {
        firestore.collection(TRANSECTION_HISTORY_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
    }

    private fun createPager(q: Query): Pager<Query, DocumentSnapshot> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
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
//        getCollectionHistory()
//        getBalance()
        Timber.i("init")
        getTodaysCollection()
    }

    fun getCollectionHistory(type: String? = null) {
        Timber.i("getCollectionHistory")
        Timber.i("type -> $type")
        viewModelScope.launch(Dispatchers.IO) {
            val query: Query
            when (type) {
                "ASC" -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("timestamp", Query.Direction.ASCENDING)
                }

                "DESC" -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                }

                "SAZ" -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("s_shopName", Query.Direction.ASCENDING)
                }

                "SZA" -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("s_shopName", Query.Direction.DESCENDING)
                }

                "UAZ" -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("s_firstName", Query.Direction.ASCENDING)
                }

                "UZA" -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("s_firstName", Query.Direction.DESCENDING)
                }

                else -> {
                    query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                }
            }
            Timber.i(query.toString())
            list = createPager(q = query).flow
        }
    }

    fun getSwipeShopsCollectionHistory() {
        Timber.i("getSwipeShopsCollectionHistory")
        _uiState.value = CollectionHistoryUiState.Refreshing
        list = createPager(q = BASE_QUERY).flow
    }


    internal fun getTodaysCollection() {
        Timber.i("getTodaysCollection")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TODAYS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(TodaysCollections::class.java)
                            ?.apply {
                                Timber.tag("addOnSuccessListener").i(it.id)
                                id = it.id
                            }
                    }.also {
                        if (it.isNotEmpty()) {
                            _todaysCollection.value = it[0].balance
                            _todaysCredit.value = it[0].credit
                            _todaysDebit.value = it[0].debit
                            Timber.tag("addOnSuccessListener").i("_todaysCollection.value -> ${_todaysCollection.value} _todaysCredit.value -> ${_todaysCredit.value} _todaysDebit.value -> ${_todaysDebit.value}")
                        }
                    }
                }
                .addOnFailureListener {
                    val error = it.message ?: "Something went wrong,Please refresh the page"
                    _todaysCollection.value = 0L
                    _uiState.value = CollectionHistoryUiState.CollectionHistoryUiStateError(error)
                    Timber.tag("addOnFailureListener").i(error)
                }
        }
    }

}