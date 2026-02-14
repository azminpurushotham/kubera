# One-Time UI Events Prompt

Use when handling toasts, snackbars, or navigation events.

---

> Use SharedFlow for one-time events (like ShopListUiEvent):
>
> - Create sealed interface `XxxUiEvent` with events (ShowError, NavigateTo, etc.)
> - ViewModel emits via `_uiEvent.emit(XxxUiEvent.ShowError(message))`
> - Screen collects in `LaunchedEffect(Unit) { viewModel.uiEvent.collectLatest { ... } }`
> - Do NOT store error/navigation in uiState - it causes re-show on config change

---

**Reference:** `ui/shoplist/ShopListUiEvent.kt`
