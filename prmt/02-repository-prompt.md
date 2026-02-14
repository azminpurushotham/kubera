# Repository Layer Prompt

Use when creating/refactoring repositories.

---

> Extract all data/API calls (Firestore, REST, Room) from ViewModel into repositories.
>
> - Create `interface XxxRepository` with suspend/Flow methods
> - Create `XxxRepositoryImpl` implementing the interface
> - Use `Result<T>` or sealed class for success/error
> - Use suspend + `await()` instead of `addOnSuccessListener` / `addOnFailureListener`
> - Add constants to `RepositoryConstants` (page sizes, debounce, error messages)
> - Keep repositories in `data/repository/`, never in `ui/`

---

**Reference:** `data/repository/ShopRepository.kt`, `TodaysCollectionRepository.kt`
