package com.collection.kubera.ui.profile

import com.collection.kubera.data.User

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ProfileUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : ProfileUiState

    /**
     * Still loading
     */
    object Loading : ProfileUiState

    data class UserCredentials(val user: User): ProfileUiState
    data class Error(val message: String): ProfileUiState
    data class UserNameError(val message: String): ProfileUiState
    data class PasswordError(val message: String): ProfileUiState
    data class ConfirmPasswordError(val message: String): ProfileUiState
    data class PasswordMismatchError(val message: String): ProfileUiState

    /**
     * Text has been generated
     */
    data class UpdationSuccess(val message: String) : ProfileUiState

    /**
     * There was an error generating text
     */
    data class UpdationFiled(val message: String) : ProfileUiState
}