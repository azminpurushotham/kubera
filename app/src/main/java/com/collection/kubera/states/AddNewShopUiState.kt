package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface AddNewShopUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : AddNewShopUiState

    /**
     * Still loading
     */
    object Loading : AddNewShopUiState

    /**
     * Text has been generated
     */
    data class AddNewShopInit(val outputText: String) : AddNewShopUiState
    /**
     * Text has been generated
     */
    data class AddNewShopSuccess(val outputText: String) : AddNewShopUiState

    /**
     * There was an error generating text
     */
    data class AddNewShopError(val errorMessage: String) : AddNewShopUiState
    /**
     * There was an error generating text
     */
    data class AddNewShopCompleted(val outputText: String) : AddNewShopUiState
}