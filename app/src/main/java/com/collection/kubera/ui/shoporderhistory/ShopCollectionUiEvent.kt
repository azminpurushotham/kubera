package com.collection.kubera.ui.shoporderhistory

/**
 * One-time UI events for shop collection history (Toast).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface ShopCollectionUiEvent {
    data class ShowError(val message: String) : ShopCollectionUiEvent
}
