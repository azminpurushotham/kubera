# ViewModel Pattern Prompt

Use when creating/refactoring ViewModels.

---

> Follow ShopListViewModel pattern:
>
> - Use `@HiltViewModel` and `@Inject constructor` for all dependencies
> - Use `StateFlow` for UI state (e.g. `_uiState`, `_data`)
> - Use `SharedFlow` for one-time events (errors, navigation) - never persist in state
> - Inject `CoroutineDispatcher` for testability (provide via Hilt)
> - No direct Firestore/API/Context in ViewModel - use repositories only
> - Use `viewModelScope.launch(dispatcher)` for async work
> - Use `Result` or sealed class for repository results, map to state/events
> - Debounce search input with `debounce()` + `distinctUntilChanged()` when applicable

---

**Reference:** `ui/shoplist/ShopListViewModel.kt`
