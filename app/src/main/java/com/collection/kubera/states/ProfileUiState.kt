package com.collection.kubera.states

import com.collection.kubera.data.User

/**
 * UI state for the Profile screen.
 * Success/error toasts are handled via ProfileUiEvent (SharedFlow).
 * Validation errors stay in state for inline form display.
 */
sealed interface ProfileUiState {
    object Initial : ProfileUiState
    object Loading : ProfileUiState
    data class UserCredentials(val user: User) : ProfileUiState
    data class UserNameError(val message: String) : ProfileUiState
    data class PasswordError(val message: String) : ProfileUiState
    data class PasswordMismatchError(val message: String) : ProfileUiState
}
