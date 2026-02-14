package com.collection.kubera.ui.profile

/**
 * One-time UI events that should be consumed once (e.g. show error, show success).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface ProfileUiEvent {
    data class ShowError(val message: String) : ProfileUiEvent
    data class ShowSuccess(val message: String) : ProfileUiEvent
}
