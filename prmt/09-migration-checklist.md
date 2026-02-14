# Feature Migration Checklist

Use when standardizing an existing feature.

---

- [ ] Create repository interface and implementation in `data/repository/`
- [ ] Move all Firestore/API calls to repository (use suspend + await)
- [ ] Create Hilt module in `di/XxxModule.kt`
- [ ] Add `@HiltViewModel` to ViewModel, inject repositories
- [ ] Create `XxxUiEvent` sealed interface for one-time events
- [ ] Replace ViewModelFactory with `hiltViewModel()` in Screen
- [ ] Add `LaunchedEffect` for init, lifecycle, uiEvent collection
- [ ] Fix pull-to-refresh: use Paging loadState to clear isRefreshing
- [ ] Remove direct Context/SharedPreferences injection - use repository or DataStore
- [ ] Add constants to RepositoryConstants
