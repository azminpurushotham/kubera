package com.collection.kubera.ui.shopdetails

/**
 * One-time UI events (Toast, navigation) - consumed once via SharedFlow.
 */
sealed interface ShopDetailsUiEvent {
    data class ShowToast(val message: String) : ShopDetailsUiEvent
    data class PopBack(val message: String) : ShopDetailsUiEvent
}
