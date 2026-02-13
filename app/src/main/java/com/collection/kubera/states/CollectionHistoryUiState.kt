package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface CollectionHistoryUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : CollectionHistoryUiState

    /**
     * Still loading
     */
    object Loading : CollectionHistoryUiState

    object Refreshing : CollectionHistoryUiState

    /**
     * Text has been generated
     */
    data class CollectionHistoryUiStateInit(val outputText: String) : CollectionHistoryUiState
    /**
     * Text has been generated
     */
    data class CollectionHistoryUiStateSuccess(val outputText: String) : CollectionHistoryUiState

    /**
     * There was an error generating text
     */
    data class CollectionHistoryUiStateError(val errorMessage: String) : CollectionHistoryUiState
}