package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ReportUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : ReportUiState

    /**
     * Still loading
     */
    object Loading : ReportUiState

    /**
     * Text has been generated
     */
    data class ReportInit(val message: String) : ReportUiState
    /**
     * Text has been generated
     */
    data class ReportSuccess(val message: String) : ReportUiState

    /**
     * There was an error generating text
     */
    data class ReportError(val errorMessage: String) : ReportUiState
    /**
     * There was an error generating text
     */
    data class ReportCompleted(val message: String) : ReportUiState
}