package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ShopDetailUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : ShopDetailUiState

    /**
     * Still loading
     */
    object Loading : ShopDetailUiState

    /**
     * Text has been generated
     */
    data class ShopDetailInit(val outputText: String) : ShopDetailUiState
    /**
     * Text has been generated
     */
    data class ShopDetailSuccess(val outputText: String) : ShopDetailUiState

    /**
     * There was an error generating text
     */
    data class ShopDetailError(val errorMessage: String) : ShopDetailUiState
}