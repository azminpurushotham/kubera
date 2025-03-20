package com.collection.kubera.ui.shoporderhistory

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.utils.FirestorePagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class ShopCollectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> get() = _balance
    private val firestore = FirebaseFirestore.getInstance()
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop
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
        createPager(
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
            .whereEqualTo("shopId", shop.value?.id)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        ).flow

    fun getSwipeShopsCollectionHistory() {
        Timber.v("getSwipeShopsCollectionHistory")
        if ((shop.value?.id?.length ?:0)  > 1) {
            val query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .whereEqualTo("shopId", shop.value?.id)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
            list = createPager(q = query).flow
        }
    }

    fun getCollectionHistory() {
        Timber.v("getCollectionHistory ${shop ?: "NONE"}")
        val query = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
            .whereEqualTo("shopId", shop.value?.id)
        list = createPager(q = query).flow
    }

    fun setShop(shop: Shop) {
        _shop.value = shop
        _balance.value = _shop.value?.balance ?: 0
    }
}