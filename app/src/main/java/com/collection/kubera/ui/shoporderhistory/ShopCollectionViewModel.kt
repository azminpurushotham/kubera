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
    lateinit var BASE_QUERY: Query

    private fun createPager(q: Query): Pager<Query, DocumentSnapshot> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FirestorePagingSource(
                    query = q
                )
            }
        )
    }

    var list: Flow<PagingData<DocumentSnapshot>> = createPager(BASE_QUERY).flow

    fun getSwipeShopsCollectionHistory() {
        Timber.v("getSwipeShopsCollectionHistory")
        list = createPager(q = BASE_QUERY).flow
    }

    fun getCollectionHistory() {
        Timber.v("getCollectionHistory ${shop ?: "NONE"}")
        list = createPager(q = BASE_QUERY).flow
    }

    fun setShop(shop: Shop) {
        _shop.value = shop
        _balance.value = _shop.value?.balance ?: 0
        BASE_QUERY = firestore.collection(TRANSECTION_HISTORY_COLLECTION)
            .whereEqualTo("shopId", _shop.value?.id)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        list = createPager(BASE_QUERY).flow
    }
}