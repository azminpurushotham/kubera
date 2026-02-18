# ViewModel Pattern Prompt

Use when creating/refactoring ViewModels.

---

## Clean Architecture

- **ViewModel injects UseCases only** — never inject repositories directly
- ViewModel coordinates use cases and maps results to UI state/events
- Business logic lives in UseCases; ViewModel only orchestrates

---

## Core Rules

- **ViewModel must not hold** Activity, Fragment, View, or Context reference
- **One ViewModel per screen** (feature-scoped)
- **ViewModel should survive configuration changes** without reloading data
- **ViewModel must not depend on lifecycle methods** (onResume/onPause/etc.)

## State & Events

- Use **StateFlow** for UI state (`_uiState`)
- Use **SharedFlow** for one-time events: `MutableSharedFlow(replay = 0, extraBufferCapacity = 2)`
- Use `emit()` from coroutines — never `tryEmit` for event emission
- **Never expose mutable state**; public state must be immutable
- **Never store** error/navigation in uiState — emit as UiEvent

## Data Layer

- **Heavy work** in UseCase/Repository, not ViewModel
- ViewModel **only coordinates** via use cases
- **Store only UI state** inside ViewModel
- **Business logic** in domain (UseCases) and data (Repositories)

## Coroutines & Scope

- **Always use viewModelScope** for coroutines
- **Never use GlobalScope**
- Use `viewModelScope.launch(dispatcher)` for async work
- Call use cases from inside `launch` — use `emit()` for SharedFlow

## Restrictions

- **Do not inject** repositories — use use cases
- **ViewModel should not perform navigation directly** — emit event
- **ViewModel should not show Toast/Snackbar directly** — emit event
- Keep ViewModel **testable** (no Android UI dependencies)

## Implementation Pattern

```kotlin
@HiltViewModel
class XxxViewModel @Inject constructor(
    private val getXxxUseCase: GetXxxUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow<XxxUiState>(...)
    private val _uiEvent = MutableSharedFlow<XxxUiEvent>(replay = 0, extraBufferCapacity = 2)
    
    fun load() {
        viewModelScope.launch(dispatcher) {
            when (val result = getXxxUseCase()) {
                is Result.Success -> _uiState.value = ...
                is Result.Error -> _uiEvent.emit(XxxUiEvent.ShowError(...))
            }
        }
    }
}
```

---

**Reference:** `ui/shoplist/ShopListViewModel.kt`, `domain/shoplist/usecase/`
