package com.collection.kubera.states

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface SettingsUiState{
    /**
     * Empty state when the screen is first shown
     */
    object Initial : SettingsUiState

    /**
     * Still loading
     */
    object Loading : SettingsUiState

    /**
     * Text has been generated
     */
    data class SettingsInit(val outputText: String) : SettingsUiState
    /**
     * Text has been generated
     */
    data class SettingsSuccess(val outputText: String) : SettingsUiState

    data class ShopDetailSuccess(val outputText: String) : SettingsUiState

    data class ShopDetailError(val errorMessage: String) : SettingsUiState

    /**
     * There was an error generating text
     */
    data class SettingsError(val errorMessage: String) : SettingsUiState
    /**
     * There was an error generating text
     */
    data class SettingsCompleted(val outputText: String) : SettingsUiState
}