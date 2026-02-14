package com.collection.kubera.ui.login

/**
 * One-time UI events that should be consumed once (e.g. show error, navigate).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface LoginUiEvent {
    data class ShowError(val message: String) : LoginUiEvent
    data class ShowSuccess(val message: String) : LoginUiEvent
    object NavigateToMain : LoginUiEvent
}
