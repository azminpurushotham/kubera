package com.collection.kubera.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.collection.kubera.data.Result
import com.collection.kubera.data.TodaysCollectionData
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.states.HomeUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val todaysCollectionRepository: TodaysCollectionRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _todaysCollection = MutableStateFlow(TodaysCollectionData(0L, 0L, 0L))
    val todaysCollection: StateFlow<TodaysCollectionData> = _todaysCollection.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopListUiEvent>()
    val uiEvent: SharedFlow<ShopListUiEvent> = _uiEvent.asSharedFlow()

    private val _listFlow = MutableStateFlow<Flow<PagingData<Shop>>>(
        shopRepository.getShopsPagingFlow().cachedIn(viewModelScope)
    )
    val list: StateFlow<Flow<PagingData<Shop>>> = _listFlow.asStateFlow()

    fun init() {
        Timber.d("init")
        syncTodaysCollection()
    }

    fun onResume() {
        Timber.d("onResume")
        syncTodaysCollection()
        onRefresh();
    }

    fun onRefresh() {
        Timber.d("onRefresh")
        refreshShops()
        syncTodaysCollection()
    }

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch(dispatcher) {
            _searchQuery
                .debounce(RepositoryConstants.SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query ->
                    val trimmed = query.trim()
                    if (trimmed.length >= RepositoryConstants.MIN_SEARCH_QUERY_LENGTH) {
                        _uiState.value = HomeUiState.Searching
                        _listFlow.value = shopRepository
                            .getShopsSearchPagingFlow(trimmed)
                            .cachedIn(viewModelScope)
                        _uiState.value = HomeUiState.HomeSuccess("Success")
                    } else if (trimmed.isEmpty()) {
                        refreshShops()
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun syncTodaysCollection() {
        viewModelScope.launch(dispatcher) {
            when (val result = todaysCollectionRepository.syncTodaysCollection()) {
                is Result.Success -> {
                    _todaysCollection.value = result.data
                }
                is Result.Error -> {
                    _todaysCollection.value = TodaysCollectionData(0L, 0L, 0L)
                    _uiEvent.emit(
                        ShopListUiEvent.ShowError(
                            result.exception.message
                                ?: RepositoryConstants.DEFAULT_ERROR_MESSAGE
                        )
                    )
                }
            }
        }
    }

    private fun refreshShops() {
        Timber.d("refreshShops")
        _listFlow.value = shopRepository.getShopsPagingFlow().cachedIn(viewModelScope)
    }

    fun getSwipeShopsOnResume() {
        Timber.d("getSwipeShopsOnResume")
        viewModelScope.launch(dispatcher) {
            _listFlow.value = shopRepository.getShopsPagingFlow().cachedIn(viewModelScope)
        }
    }
}
