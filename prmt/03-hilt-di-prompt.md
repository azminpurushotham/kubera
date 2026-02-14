# Hilt Dependency Injection Prompt

Use when adding Hilt to a feature.

---

> Add Hilt for this feature following shoplist pattern:
>
> 1. Create `XxxModule.kt` in `di/` with `@Module` and `@InstallIn(SingletonComponent::class)`
> 2. Provide repositories as `@Singleton`
> 3. Provide `CoroutineDispatcher` if ViewModel needs injectable dispatcher: `@Provides fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO`
> 4. Annotate ViewModel with `@HiltViewModel` and `@Inject constructor`
> 5. In Screen, use `hiltViewModel()` instead of `viewModel(factory = ...)`
> 6. Remove manual ViewModelFactory for this feature

---

**Reference:** `di/ShopListModule.kt`
