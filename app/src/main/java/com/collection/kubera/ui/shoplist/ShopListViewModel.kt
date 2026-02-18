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
import com.collection.kubera.domain.shoplist.usecase.GetShopsPagingUseCase
import com.collection.kubera.domain.shoplist.usecase.SearchShopsUseCase
import com.collection.kubera.domain.shoplist.usecase.SyncTodaysCollectionUseCase
import com.collection.kubera.states.HomeUiState
import kotlinx.coroutines.CoroutineDispatcher
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
    private val getShopsPagingUseCase: GetShopsPagingUseCase,
    private val searchShopsUseCase: SearchShopsUseCase,
    private val syncTodaysCollectionUseCase: SyncTodaysCollectionUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _todaysCollection = MutableStateFlow(TodaysCollectionData(0L, 0L, 0L))
    val todaysCollection: StateFlow<TodaysCollectionData> = _todaysCollection.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopListUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<ShopListUiEvent> = _uiEvent.asSharedFlow()

    private val _listFlow = MutableStateFlow(
        getShopsPagingUseCase().cachedIn(viewModelScope)
    )
    val list: StateFlow<Flow<PagingData<Shop>>> = _listFlow.asStateFlow()

    init {
        Timber.d("ShopListViewModel: initial load")
        syncTodaysCollection()
    }

    fun onScreenResumed() {
        Timber.d("onScreenResumed")
        syncTodaysCollection()
    }

    fun refreshWhenVisible() {
        Timber.d("onRefresh")
        refreshShops()
        syncTodaysCollection()
    }

    private val _searchQuery = MutableStateFlow("")

    init {
        Timber.d("ShopListViewModel init: starting search query observer")
        viewModelScope.launch(dispatcher) {
            _searchQuery
                .debounce(RepositoryConstants.SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query ->
                    val trimmed = query.trim()
                    Timber.d("ShopListViewModel searchQuery collect: trimmed='$trimmed' length=${trimmed.length}")
                    if (trimmed.length >= RepositoryConstants.MIN_SEARCH_QUERY_LENGTH) {
                        Timber.d("ShopListViewModel: search with query='$trimmed'")
                        _uiState.value = HomeUiState.Searching
                        _listFlow.value = searchShopsUseCase(trimmed).cachedIn(viewModelScope)
                        _uiState.value = HomeUiState.HomeSuccess("Success")
                    } else if (trimmed.isEmpty()) {
                        Timber.d("ShopListViewModel: query empty, refreshing shops")
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
            when (val result = syncTodaysCollectionUseCase()) {
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
        _listFlow.value = getShopsPagingUseCase().cachedIn(viewModelScope)
    }

}
