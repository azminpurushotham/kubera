package com.collection.kubera.ui.landing

/**
 * One-time UI events for landing (navigation).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-triggering navigation on configuration changes.
 */
sealed interface LandingUiEvent {
    object NavigateToMain : LandingUiEvent
    object NavigateToLogin : LandingUiEvent
}
