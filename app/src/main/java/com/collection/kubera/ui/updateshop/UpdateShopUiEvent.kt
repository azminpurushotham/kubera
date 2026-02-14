package com.collection.kubera.ui.updateshop

/**
 * One-time UI events for update shop (Toast, navigation).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface UpdateShopUiEvent {
    data class ShowError(val message: String) : UpdateShopUiEvent
    data class ShowSuccess(val message: String) : UpdateShopUiEvent
    object NavigateBack : UpdateShopUiEvent
}
