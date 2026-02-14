package com.collection.kubera.ui.orderhistory

/**
 * One-time UI events that should be consumed once (e.g. show error).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface CollectionHistoryUiEvent {
    data class ShowError(val message: String) : CollectionHistoryUiEvent
}
