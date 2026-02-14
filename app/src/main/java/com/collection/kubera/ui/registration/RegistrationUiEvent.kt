package com.collection.kubera.ui.registration

import com.collection.kubera.data.User

/**
 * One-time UI events for registration (Toast, navigation).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface RegistrationUiEvent {
    data class ShowError(val message: String) : RegistrationUiEvent
    data class NavigateToUpdateCredentials(val user: User) : RegistrationUiEvent
}
