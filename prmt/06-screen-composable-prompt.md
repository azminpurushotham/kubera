# Screen (Composable) Pattern Prompt

Use when creating/refactoring Compose screens.

---

> Follow ShopListScreen pattern:
>
> - ViewModel: `viewModel: XxxViewModel = hiltViewModel()`
> - Use `LaunchedEffect(Unit)` for one-time init (`viewModel.init()`)
> - Use `LaunchedEffect(lifecycleState)` for `onResume` when needed
> - Use `LaunchedEffect(Unit)` to collect `viewModel.uiEvent` and handle (Toast, Snackbar, navigate)
> - Use `collectAsState()` for StateFlow, `collectAsLazyPagingItems()` for Paging
> - For PullToRefresh: set `isRefreshing = true` in onRefresh, use `LaunchedEffect(loadState.refresh)` to set `isRefreshing = false` when `loadState !is LoadState.Loading`
> - Use `DisposableEffect` for lifecycle observer when tracking ON_RESUME
> - Keep Screen dumb - no business logic, only UI composition and state collection

---

**Reference:** `ui/shoplist/ShopListScreen.kt`
