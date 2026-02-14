package com.collection.kubera.states

/**
 * UI state for the AddNewShop screen.
 * Success/error messages and navigation are handled via AddNewShopUiEvent (SharedFlow).
 */
sealed interface AddNewShopUiState {
    object Initial : AddNewShopUiState
    object Loading : AddNewShopUiState
}