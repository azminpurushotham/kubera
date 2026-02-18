package com.collection.kubera.ui.shoporderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Shop
import com.collection.kubera.data.mapper.toDataCollectionModel
import com.collection.kubera.domain.shoporderhistory.usecase.GetShopCollectionHistoryPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShopCollectionViewModel @Inject constructor(
    private val getShopCollectionHistoryPagingUseCase: GetShopCollectionHistoryPagingUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _balance = MutableStateFlow<Long>(0L)
    val balance: StateFlow<Long> = _balance.asStateFlow()

    private val _listFlow = MutableStateFlow(
        getShopCollectionHistoryPagingUseCase("").map { it.map { m -> m.toDataCollectionModel() } }.cachedIn(viewModelScope)
    )
    val listFlow: StateFlow<Flow<PagingData<CollectionModel>>> = _listFlow.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopCollectionUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<ShopCollectionUiEvent> = _uiEvent.asSharedFlow()

    fun init(shop: Shop) {
        Timber.d("init shop=${shop.id}")
        _shop.value = shop
        _balance.value = shop.balance ?: 0L
        viewModelScope.launch(dispatcher) {
            _listFlow.value = getShopCollectionHistoryPagingUseCase(shop.id).map { it.map { m -> m.toDataCollectionModel() } }.cachedIn(viewModelScope)
        }
    }

    fun onRefresh() {
        Timber.d("onRefresh")
        _shop.value?.let { currentShop ->
            viewModelScope.launch(dispatcher) {
                _listFlow.value = getShopCollectionHistoryPagingUseCase(currentShop.id).map { it.map { m -> m.toDataCollectionModel() } }.cachedIn(viewModelScope)
            }
        }
    }
}
