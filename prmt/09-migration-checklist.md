# Feature Migration Checklist

Use when standardizing an existing feature (Clean Architecture).

---

- [ ] Create **repository interface** in `data/repository/`
- [ ] Create **repository impl** in `genaral/` or `cloud/` (flavor-specific)
- [ ] Move all Firestore/API/Room calls to repository (use suspend + await)
- [ ] Create **use cases** in `domain/[feature]/usecase/` (one per operation)
- [ ] UseCases inject repositories; expose `operator fun invoke(...)` or `invoke(): Flow<...>`
- [ ] Create Hilt module in `di/XxxModule.kt` (if needed)
- [ ] Add `@HiltViewModel` to ViewModel, **inject UseCases only** (not repositories)
- [ ] Create `XxxUiEvent` sealed interface for one-time events
- [ ] Use `MutableSharedFlow(extraBufferCapacity = 2)` and `emit()` for UiEvent
- [ ] Replace ViewModelFactory with `hiltViewModel()` in Screen
- [ ] Add `LaunchedEffect` for init, lifecycle, uiEvent collection
- [ ] Place `XxxUiState` in `states/` if not already there
- [ ] Add constants to `RepositoryConstants` where applicable
- [ ] Fix pull-to-refresh: use Paging loadState to clear isRefreshing
- [ ] Remove direct Context/SharedPreferences — use repository or DataStore
