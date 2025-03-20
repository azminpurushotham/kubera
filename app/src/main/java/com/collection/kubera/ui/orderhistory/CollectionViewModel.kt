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
import com.google.firebase.firestore.QuerySnapshot
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
    val pageLimit = 10L
    private fun createPager(q: Query): Pager<QuerySnapshot, DocumentSnapshot> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FirestorePagingSource(
                    query = q,
                    limit = pageLimit
                )
            }
        )
    }

    var list: Flow<PagingData<DocumentSnapshot>> =
        createPager(firestore.collection(TRANSECTION_HISTORY_COLLECTION)).flow

    fun init() {
//        getCollectionHistory()
//        getBalance()
        getTodaysCollection()
    }

    fun getCollectionHistory(type: String? = null) {
        Timber.v("getCollectionHistory")
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
            list = createPager(q = query).flow
        }
    }

    fun getSwipeShopsCollectionHistory() {
        Timber.v("getSwipeShopsCollectionHistory")
        _uiState.value = CollectionHistoryUiState.Refreshing
        viewModelScope.launch(Dispatchers.IO) {
            val query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
            list = createPager(q = query).flow
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
                    _uiState.value = CollectionHistoryUiState.CollectionHistoryUiStateError(
                        it.message ?: "Something went wrong,Please refresh the page"
                    )
                }
        }
    }

}