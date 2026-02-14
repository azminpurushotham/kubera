package com.collection.kubera.ui.addnewshop

/**
 * One-time UI events that should be consumed once (e.g. show error, show success, navigate).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts on configuration changes.
 */
sealed interface AddNewShopUiEvent {
    data class ShowError(val message: String) : AddNewShopUiEvent
    data class ShowSuccess(val message: String) : AddNewShopUiEvent
    object NavigateBack : AddNewShopUiEvent
}
