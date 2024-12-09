package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface HomeUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : HomeUiState

    /**
     * Still loading
     */
    object Loading : HomeUiState

    object Searching : HomeUiState

    /**
     * Text has been generated
     */
    data class HomeInit(val outputText: String) : HomeUiState
    /**
     * Text has been generated
     */
    data class HomeSuccess(val outputText: String) : HomeUiState

    /**
     * There was an error generating text
     */
    data class HomeError(val errorMessage: String) : HomeUiState
}