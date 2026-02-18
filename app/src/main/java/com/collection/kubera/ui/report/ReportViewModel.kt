package com.collection.kubera.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Result
import com.collection.kubera.data.Shop
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.report.usecase.DeleteOldReportFileUseCase
import com.collection.kubera.domain.report.usecase.GetAllShopsUseCase
import com.collection.kubera.domain.report.usecase.GetBalanceUseCase
import com.collection.kubera.domain.report.usecase.GetCollectionHistoryByDateRangeUseCase
import com.collection.kubera.states.ReportUiState
import com.collection.kubera.utils.getCurrentDate
import com.collection.kubera.utils.getTodayStartAndEndTime
import com.collection.kubera.utils.toEndTimestamp
import com.collection.kubera.utils.toTimestamp
import com.collection.kubera.utils.writeCsvFile
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getCollectionHistoryByDateRangeUseCase: GetCollectionHistoryByDateRangeUseCase,
    private val getAllShopsUseCase: GetAllShopsUseCase,
    private val deleteOldReportFileUseCase: DeleteOldReportFileUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Initial)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> = _balance.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ReportUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<ReportUiEvent> = _uiEvent.asSharedFlow()

    fun init() {
        Timber.d("init")
        syncBalance()
    }

    fun syncBalance() {
        Timber.d("syncBalance: calling getBalanceUseCase")
        viewModelScope.launch(dispatcher) {
            when (val result = getBalanceUseCase()) {
                is Result.Success -> {
                    Timber.d("syncBalance: Success balance=${result.data}")
                    _balance.value = result.data
                }
                is Result.Error -> {
                    Timber.e(result.exception, "syncBalance: Error")
                    _balance.value = 0L
                    _uiEvent.emit(
                        ReportUiEvent.ShowError(
                            result.exception.message ?: RepositoryConstants.DEFAULT_ERROR_MESSAGE
                        )
                    )
                }
            }
            Timber.d("syncBalance: completed")
        }
    }

    fun todaysReport(path: String, date: String = getCurrentDate()) {
        Timber.d("todaysReport")
        _uiState.value = ReportUiState.Loading
        val dir = File(path)
        if (!dir.exists() && !dir.mkdirs()) {
            viewModelScope.launch(dispatcher) {
                _uiEvent.emit(
                    ReportUiEvent.ShowError(
                        "${RepositoryConstants.REPORT_DIR_NOT_CREATED} ${dir.name}"
                    )
                )
            }
            _uiState.value = ReportUiState.Initial
            return
        }

        val (startTimestamp, endTimestamp) = getTodayStartAndEndTime()

        viewModelScope.launch(dispatcher) {
            when (val result = getCollectionHistoryByDateRangeUseCase(
                startTimestamp,
                endTimestamp
            )) {
                is Result.Success -> {
                    val list = result.data
                    if (list.isNotEmpty()) {
                        try {
                            writeCsvFile(
                                fileName = "$date.csv",
                                dir.absolutePath,
                                list = list,
                                schema = getReportSchema()
                            )
                            _uiEvent.emit(
                                ReportUiEvent.ShowSuccess("Today's $date report generated successfully")
                            )
                        } catch (e: Exception) {
                            _uiEvent.emit(
                                ReportUiEvent.ShowError("Failed to create today's report $date ERROR $e")
                            )
                        }
                    } else {
                        _uiEvent.emit(
                            ReportUiEvent.ShowError("No collection report for today $date")
                        )
                    }
                }
                is Result.Error -> {
                    _uiEvent.emit(
                        ReportUiEvent.ShowError(
                            "No collection report for today $date ${result.exception.message}"
                        )
                    )
                }
            }
            _uiState.value = ReportUiState.Initial
        }
    }

    fun generateReport(path: String, startDate: String, endDate: String) {
        Timber.d("generateReport")
        _uiState.value = ReportUiState.Loading
        val fileName = "$startDate-$endDate.csv"
        val dir = File(path)
        if (!dir.exists() && !dir.mkdirs()) {
            viewModelScope.launch(dispatcher) {
                _uiEvent.emit(
                    ReportUiEvent.ShowError(
                        "${RepositoryConstants.REPORT_DIR_NOT_CREATED} ${dir.name}"
                    )
                )
            }
            _uiState.value = ReportUiState.Initial
            return
        }

        val startTimestamp = startDate.toTimestamp()
        val endTimestamp = endDate.toEndTimestamp()

        if (startTimestamp == null || endTimestamp == null) {
            viewModelScope.launch(dispatcher) {
                _uiEvent.emit(ReportUiEvent.ShowError(RepositoryConstants.REPORT_INVALID_DATE_FORMAT))
            }
            _uiState.value = ReportUiState.Initial
            return
        }

        viewModelScope.launch(dispatcher) {
            when (val result = getCollectionHistoryByDateRangeUseCase(
                startTimestamp,
                endTimestamp
            )) {
                is Result.Success -> {
                    val list = result.data
                    if (list.isNotEmpty()) {
                        try {
                            writeCsvFile(
                                fileName = fileName,
                                dir.absolutePath,
                                list = list,
                                schema = getReportSchema()
                            )
                            _uiEvent.emit(
                                ReportUiEvent.ShowSuccess("$fileName report generated successfully")
                            )
                        } catch (e: Exception) {
                            _uiEvent.emit(
                                ReportUiEvent.ShowError("Failed to create report for $fileName ERROR $e")
                            )
                        }
                    } else {
                        _uiEvent.emit(
                            ReportUiEvent.ShowError("No collection report for $fileName")
                        )
                    }
                }
                is Result.Error -> {
                    _uiEvent.emit(
                        ReportUiEvent.ShowError(
                            "No collection report for $fileName ${result.exception.message}"
                        )
                    )
                }
            }
            _uiState.value = ReportUiState.Initial
        }
    }

    fun generateAllShops(path: String) {
        Timber.d("generateAllShops")
        _uiState.value = ReportUiState.Loading
        val dir = File(path)
        if (!dir.exists() && !dir.mkdirs()) {
            viewModelScope.launch(dispatcher) {
                _uiEvent.emit(
                    ReportUiEvent.ShowError(
                        "${RepositoryConstants.REPORT_DIR_NOT_CREATED} ${dir.name}"
                    )
                )
            }
            _uiState.value = ReportUiState.Initial
            return
        }

        viewModelScope.launch(dispatcher) {
            when (val result = getAllShopsUseCase()) {
                is Result.Success -> {
                    val list = result.data
                    if (list.isNotEmpty()) {
                        try {
                            writeCsvFile(
                                fileName = RepositoryConstants.REPORT_ALL_SHOPS_FILENAME,
                                dir.absolutePath,
                                list = list,
                                schema = getShopsSchema()
                            )
                            _uiEvent.emit(
                                ReportUiEvent.ShowSuccess("AllShops report generated successfully")
                            )
                        } catch (e: FileNotFoundException) {
                            Timber.tag("FileNotFoundException").d("DELETE -> ${dir.delete()}")
                            if (!dir.delete()) {
                                deleteOldReportFileUseCase(dir.name)?.let { msg ->
                                    _uiEvent.emit(ReportUiEvent.ShowError(msg))
                                } ?: run {
                                    _uiEvent.emit(
                                        ReportUiEvent.ShowError(
                                            "Please DELETE old folder \"${dir.name}\" and try again"
                                        )
                                    )
                                }
                            } else {
                                _uiEvent.emit(ReportUiEvent.ShowError("Please try again"))
                            }
                        } catch (e: Exception) {
                            _uiEvent.emit(
                                ReportUiEvent.ShowError(
                                    "Failed to create AllShops report ERROR $e\n\nPlease DELETE old folder \"${dir.name}\" and try again"
                                )
                            )
                        }
                    } else {
                        _uiEvent.emit(
                            ReportUiEvent.ShowError("Failed to create AllShops report")
                        )
                    }
                }
                is Result.Error -> {
                    _uiEvent.emit(
                        ReportUiEvent.ShowError("Failed to create AllShops report ${result.exception.message}")
                    )
                }
            }
            _uiState.value = ReportUiState.Initial
        }
    }

    private fun getReportSchema(): CsvSchema = CsvSchema.builder()
        .addColumn("Time")
        .addColumn("ShopName")
        .addColumn("FirstName")
        .addColumn("LastName")
        .addColumn("TransactionType")
        .addColumn("Amount")
        .addColumn("CollectedBy")
        .addColumn("stability")
        .build()

    private fun getShopsSchema(): CsvSchema = CsvSchema.builder()
        .addColumn("ShopName")
        .addColumn("FirstName")
        .addColumn("LastName")
        .addColumn("PhoneNumber")
        .addColumn("Balance")
        .addColumn("TransactionType")
        .addColumn("Time")
        .addColumn("stability")
        .build()
}
