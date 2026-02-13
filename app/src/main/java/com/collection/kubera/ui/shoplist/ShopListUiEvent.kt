package com.collection.kubera.ui.shoplist

/**
 * One-time UI events that should be consumed once (e.g. show error, navigate).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts/snackbars on configuration changes.
 */
sealed interface ShopListUiEvent {
    data class ShowError(val message: String) : ShopListUiEvent
}
