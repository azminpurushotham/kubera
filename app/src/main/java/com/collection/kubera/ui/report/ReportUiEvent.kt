package com.collection.kubera.ui.report

/**
 * One-time UI events that should be consumed once (e.g. show error, show success).
 * Using SharedFlow prevents events from persisting in state and avoids
 * re-showing toasts/snackbars on configuration changes.
 */
sealed interface ReportUiEvent {
    data class ShowError(val message: String) : ReportUiEvent
    data class ShowSuccess(val message: String) : ReportUiEvent
}
