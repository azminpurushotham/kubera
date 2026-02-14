package com.collection.kubera.states

/**
 * UI state for the Report screen.
 * Success/error messages are handled via ReportUiEvent (SharedFlow) to avoid re-show on config change.
 */
sealed interface ReportUiState {
    object Initial : ReportUiState
    object Loading : ReportUiState
}