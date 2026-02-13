package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface RegistrationUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : RegistrationUiState

    /**
     * Still loading
     */
    object Loading : RegistrationUiState

    /**
     * Text has been generated
     */
    data class RegistrationInit(val outputText: String) : RegistrationUiState
    /**
     * Text has been generated
     */
    data class RegistrationSuccess(val outputText: String) : RegistrationUiState

    /**
     * There was an error generating text
     */
    data class RegistrationError(val errorMessage: String) : RegistrationUiState
}