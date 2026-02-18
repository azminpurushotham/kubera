# Architecture Overview

**Clean Architecture** — ViewModel → UseCase → Repository

Reference: **`ui/shoplist/`**, **`domain/shoplist/usecase/`**

---

## Dependency Flow

```
UI (Screen, ViewModel)  →  Domain (Use Cases)  →  Data (Repository interfaces)
                                                         ↓
                                               Impls (genaral/cloud)
```

- **UI** never depends on Data directly (except models like User, Shop)
- **ViewModel** injects **UseCases only**, never repositories
- **UseCases** inject repositories and orchestrate business logic
- **Data** provides repository interfaces (main) and implementations (flavor-specific)

---

## Layer Structure

```
domain/[feature]/
└── usecase/
    ├── GetXxxUseCase.kt      # ViewModel → UseCase → Repository
    ├── SearchXxxUseCase.kt
    └── SyncXxxUseCase.kt

data/
├── repository/               # Interface in main, Impl in genaral/cloud
│   ├── XxxRepository.kt
│   └── (XxxRepositoryImpl in flavor source sets)
├── XxxData.kt                # Models (User, Shop, Result)
└── ...

di/
├── XxxModule.kt              # Hilt module per feature
└── ...

ui/[feature]/
├── XxxScreen.kt              # Composable, uses hiltViewModel()
├── XxxViewModel.kt           # @HiltViewModel, injects UseCases only
├── XxxUiEvent.kt             # One-time events (SharedFlow)
└── ...

states/                       # All UiState sealed types (shared)
├── XxxUiState.kt
└── ...
```

---

## Rules

1. **ViewModel** → UseCases only. No repository injection.
2. **UseCase** → Repository interfaces. One business operation per use case.
3. **Repository** → Interface in main; implementation in genaral or cloud flavor.
4. **UI** → ViewModel; never data layer.
