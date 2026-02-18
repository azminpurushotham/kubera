# Hilt Dependency Injection Prompt

Use when adding Hilt to a feature.

---

> Add Hilt for this feature following Clean Architecture:
>
> 1. Create `XxxModule.kt` in `di/` with `@Module` and `@InstallIn(SingletonComponent::class)`
> 2. Provide repositories in flavor modules (`LocalDataModule` / `CloudDataModule`)
> 3. Provide `CoroutineDispatcher`: `@Provides fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO` (in ShopListModule)
> 4. **UseCases** use `@Inject constructor` — Hilt provides them automatically
> 5. **ViewModel** injects **UseCases only** (never repositories): `@HiltViewModel` + `@Inject constructor`
> 6. In Screen, use `hiltViewModel()` instead of `viewModel(factory = ...)`
> 7. Remove manual ViewModelFactory for this feature

---

**Flow:** ViewModel → UseCase → Repository (all constructor-injected)

**Reference:** `di/ShopListModule.kt`, `ui/shoplist/ShopListViewModel.kt`, `domain/shoplist/usecase/`
