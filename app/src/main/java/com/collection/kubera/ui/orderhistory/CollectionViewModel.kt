package com.collection.kubera.ui.orderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.mapper.toDataCollectionModel
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.TodaysCollectionData
import com.collection.kubera.domain.repository.TransactionSortType
import com.collection.kubera.domain.orderhistory.usecase.GetTransactionHistoryPagingUseCase
import com.collection.kubera.domain.shoplist.usecase.SyncTodaysCollectionUseCase
import com.collection.kubera.states.CollectionHistoryUiState
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
class CollectionViewModel @Inject constructor(
    private val getTransactionHistoryPagingUseCase: GetTransactionHistoryPagingUseCase,
    private val syncTodaysCollectionUseCase: SyncTodaysCollectionUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<CollectionHistoryUiState>(CollectionHistoryUiState.Initial)
    val uiState: StateFlow<CollectionHistoryUiState> = _uiState.asStateFlow()

    private val _todaysCollection = MutableStateFlow(TodaysCollectionData(0L, 0L, 0L))
    val todaysCollection: StateFlow<TodaysCollectionData> = _todaysCollection.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CollectionHistoryUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<CollectionHistoryUiEvent> = _uiEvent.asSharedFlow()

    private var currentSortType = TransactionSortType.TIMESTAMP_DESC

    private val _listFlow = MutableStateFlow<Flow<PagingData<CollectionModel>>>(
        getTransactionHistoryPagingUseCase(currentSortType).map { it.map { m -> m.toDataCollectionModel() } }.cachedIn(viewModelScope)
    )
    val list: StateFlow<Flow<PagingData<CollectionModel>>> = _listFlow.asStateFlow()

    fun init() {
        Timber.d("init")
        syncTodaysCollection()
    }

    fun onRefresh() {
        Timber.d("onRefresh")
        refreshTransactionHistory()
        syncTodaysCollection()
    }

    fun getCollectionHistory(sortType: TransactionSortType) {
        Timber.d("getCollectionHistory sortType=${sortType.code}")
        currentSortType = sortType
        viewModelScope.launch(dispatcher) {
            _listFlow.value = getTransactionHistoryPagingUseCase(sortType).map { it.map { m -> m.toDataCollectionModel() } }.cachedIn(viewModelScope)
        }
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
                        CollectionHistoryUiEvent.ShowError(
                            result.exception.message
                                ?: RepositoryConstants.DEFAULT_ERROR_MESSAGE
                        )
                    )
                }
            }
        }
    }

    private fun refreshTransactionHistory() {
        Timber.d("refreshTransactionHistory")
        _listFlow.value = getTransactionHistoryPagingUseCase(currentSortType).map { it.map { m -> m.toDataCollectionModel() } }.cachedIn(viewModelScope)
    }
}
