# Repository Layer Prompt

Use when creating/refactoring repositories.

---

> Extract all data/API calls (Firestore, REST, Room) from use cases into repositories.
>
> - Create `interface XxxRepository` in `data/repository/` with suspend/Flow methods
> - Create `XxxRepositoryImpl` in `genaral/` or `cloud/` (flavor-specific)
> - Use `Result<T>` for success/error wrapping
> - Use suspend + `await()` instead of `addOnSuccessListener` / `addOnFailureListener`
> - Add constants to `RepositoryConstants` (page sizes, debounce, error messages)
> - **Repositories are used by UseCases**, never injected into ViewModels
> - Keep repository interfaces in `data/repository/`, impls in flavor source sets

---

**Clean Architecture:** `Data` layer. ViewModel → UseCase → Repository

**Reference:** `data/repository/ShopRepository.kt`, `domain/shoplist/usecase/GetShopsPagingUseCase.kt`
