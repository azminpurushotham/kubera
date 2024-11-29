package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UpdateCredentialsUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : UpdateCredentialsUiState

    /**
     * Still loading
     */
    object Loading : UpdateCredentialsUiState

    data class UserNameError(val message: String): UpdateCredentialsUiState
    data class PasswordError(val message: String): UpdateCredentialsUiState
    data class ConfirmPasswordError(val message: String): UpdateCredentialsUiState
    data class PasswordMismatchError(val message: String): UpdateCredentialsUiState

    /**
     * Text has been generated
     */
    data class UpdationSuccess(val message: String) : UpdateCredentialsUiState

    /**
     * There was an error generating text
     */
    data class UpdationFiled(val message: String) : UpdateCredentialsUiState
}