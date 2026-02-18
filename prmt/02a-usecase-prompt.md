# Use Case Layer Prompt

Use when creating/refactoring use cases (Clean Architecture domain layer).

---

> Create use cases in `domain/[feature]/usecase/`:
>
> - One use case per business operation (e.g. GetShopsPagingUseCase, LoginUseCase)
> - Use `@Inject constructor` — Hilt provides use cases automatically
> - Use `operator fun invoke(...)` for synchronous or single-call operations
> - Use `operator fun invoke(): Flow<T>` for reactive (paging) operations
> - Use cases inject **repositories** (or helpers); never inject ViewModels or Screens
> - Map repository `Result<T>` through; keep business logic here, not in ViewModel
> - Use cases are **suspend** when calling suspend repository methods

---

**Example:**

```kotlin
class GetShopsPagingUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<PagingData<Shop>> = shopRepository.getShopsPagingFlow()
}

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(userName: String, password: String): Result<String?> {
        return when (val result = userRepository.login(userName, password)) {
            is Result.Success -> result.data?.let { userId ->
                userPreferencesRepository.saveLoginState(userId, userName, password)
                Result.Success(userId)
            } ?: Result.Error(Exception("Invalid credentials"))
            is Result.Error -> result
        }
    }
}
```

---

**Reference:** `domain/shoplist/usecase/`, `domain/login/usecase/LoginUseCase.kt`
