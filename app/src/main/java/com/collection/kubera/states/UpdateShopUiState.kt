package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UpdateShopUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : UpdateShopUiState

    /**
     * Still loading
     */
    object Loading : UpdateShopUiState

    /**
     * Text has been generated
     */
    data class UpdateShopInit(val outputText: String) : UpdateShopUiState
    /**
     * Text has been generated
     */
    data class UpdateShopSuccess(val outputText: String) : UpdateShopUiState

    data class ShopDetailSuccess(val outputText: String) : UpdateShopUiState

    data class ShopDetailError(val errorMessage: String) : UpdateShopUiState

    /**
     * There was an error generating text
     */
    data class UpdateShopError(val errorMessage: String) : UpdateShopUiState
    /**
     * There was an error generating text
     */
    data class UpdateShopCompleted(val outputText: String) : UpdateShopUiState
}