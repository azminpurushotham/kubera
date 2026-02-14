package com.collection.kubera.states

/**
 * UI state for the Login screen.
 * Success/error toasts and navigation are handled via LoginUiEvent (SharedFlow).
 */
sealed interface LoginUiState {
    object Initial : LoginUiState
    object Loading : LoginUiState
}