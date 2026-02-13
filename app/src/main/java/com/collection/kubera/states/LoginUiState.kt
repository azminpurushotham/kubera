package com.collection.kubera.states

import com.collection.kubera.data.User

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface LoginUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : LoginUiState

    /**
     * Still loading
     */
    object Loading : LoginUiState

    data class UserCredentials(val user: User): LoginUiState
    data class UserNameError(val message: String): LoginUiState
    data class PasswordError(val message: String): LoginUiState

    /**
     * Text has been generated
     */
    data class LoginSuccess(val message: String) : LoginUiState

    /**
     * There was an error generating text
     */
    data class LoginFiled(val message: String) : LoginUiState
}