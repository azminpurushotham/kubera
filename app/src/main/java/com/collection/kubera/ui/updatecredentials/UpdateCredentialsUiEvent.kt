package com.collection.kubera.ui.updatecredentials

/**
 * One-time UI events for update credentials (Toast, navigation).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface UpdateCredentialsUiEvent {
    data class ShowError(val message: String) : UpdateCredentialsUiEvent
    data class ShowSuccess(val message: String) : UpdateCredentialsUiEvent
    object NavigateToLogin : UpdateCredentialsUiEvent
}
