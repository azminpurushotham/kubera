# ViewModel Pattern Prompt

Use when creating/refactoring ViewModels.

---

## Core Rules

- **ViewModel must not hold** Activity, Fragment, View, or Context reference
- **One ViewModel per screen** (feature-scoped)
- **ViewModel should survive configuration changes** without reloading data
- **ViewModel should be lifecycle independent** — UI notifies ViewModel about lifecycle events
- **ViewModel must not depend on lifecycle methods** (onResume/onPause/etc.)

## State & Events

- **UI actions** should be emitted as events, not executed inside ViewModel
- **Never expose mutable state** from ViewModel
- **Public state must be immutable**, private state mutable
- Use **StateFlow** (state) and **SharedFlow** (events)
- **UI reads state**, ViewModel updates state — **UI must not modify ViewModel data directly**
- Avoid **MutableList** exposure — expose **List** only
- Update state using **new object reference** (copy/replace, not mutate)
- Maintain **single source of truth** inside ViewModel

## Data Layer

- **Heavy work** must be inside Repository/UseCase, not ViewModel
- ViewModel **only coordinates data**, not fetch directly from API layer
- **Store only UI state** inside ViewModel
- **Business logic** belongs to domain/repository layers

## Coroutines & Scope

- **Always use viewModelScope** for coroutines
- **Never use GlobalScope** in ViewModel
- **Cancel running jobs** automatically using viewModelScope
- Do not create **long-running blocking operations** in ViewModel

## Restrictions

- **Do not store** adapters, views, bitmaps, or large caches in ViewModel
- **ViewModel should not perform navigation directly** (emit event instead)
- **ViewModel should not show Toast/Snackbar directly**
- **Handle errors in ViewModel**, display in UI
- Keep ViewModel **free from Android framework UI classes**
- Keep ViewModel **testable** (no Android dependencies)

## Implementation Pattern

- Use `@HiltViewModel` and `@Inject constructor` for all dependencies
- Use `StateFlow` for UI state (e.g. `_uiState`, `_data`)
- Use `SharedFlow` for one-time events (errors, navigation) — never persist in state
- Inject `CoroutineDispatcher` for testability (provide via Hilt)
- No direct Firestore/API/Context in ViewModel — use repositories only
- Use `viewModelScope.launch(dispatcher)` for async work
- Use `Result` or sealed class for repository results, map to state/events
- **Initial data loading** should happen in `init {}`
- Debounce search input with `debounce()` + `distinctUntilChanged()` when applicable

---

**Reference:** `ui/shoplist/ShopListViewModel.kt`
