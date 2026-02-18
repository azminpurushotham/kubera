# One-Time UI Events Prompt

Use when handling toasts, snackbars, or navigation events.

---

> Use SharedFlow for one-time events (Clean Architecture pattern):
>
> - Create sealed interface `XxxUiEvent` with events: `ShowError`, `ShowSuccess`, `NavigateTo`, etc.
> - Use `MutableSharedFlow<XxxUiEvent>(replay = 0, extraBufferCapacity = 2)` in ViewModel
> - Emit from coroutines only: `_uiEvent.emit(XxxUiEvent.ShowError(message))` — never `tryEmit`
> - Screen collects in `LaunchedEffect(Unit) { viewModel.uiEvent.collectLatest { ... } }`
> - Do **not** store error/navigation in uiState — causes re-show on config change
> - Wrap sync emission in `viewModelScope.launch { _uiEvent.emit(...) }` when not already in a coroutine

---

**Reference:** `ui/shoplist/ShopListUiEvent.kt`, `ui/shoplist/ShopListViewModel.kt`
