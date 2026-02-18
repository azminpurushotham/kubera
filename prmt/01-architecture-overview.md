# Architecture Overview

**Clean Architecture** — ViewModel → UseCase → Repository

Reference: **`ui/shoplist/`**, **`domain/shoplist/usecase/`**

---

## Dependency Flow

```
UI (Screen, ViewModel)  →  Domain (Use Cases, Models, Repository interfaces)  ←  Data (Implementations)
```

- **Domain** is the core: pure models, repository interfaces, use cases. No framework dependencies.
- **Data** implements domain interfaces; returns domain models (via mappers from entities/DTOs).
- **UI** depends on ViewModel; ViewModels convert domain to data models only at display/navigation boundaries.
- **ViewModel** injects **UseCases only**, never repositories.

---

## Layer Structure

```
domain/
├── model/                    # Pure Kotlin models (User, Shop, CollectionModel, Result)
├── repository/               # Repository interfaces (return domain models)
│   ├── UserRepository.kt
│   ├── ShopRepository.kt
│   └── ...
└── [feature]/
    └── usecase/
        ├── GetXxxUseCase.kt
        └── ...

data/
├── mapper/                   # domain ↔ data (EntityToDomain, DomainToData, DomainToEntity)
├── repository/               # Impls in genaral/cloud, RepositoryConstants
├── User.kt, Shop.kt, ...     # Data models (Parcelable, Firestore, JSON)
└── ...

ui/[feature]/
├── XxxScreen.kt
├── XxxViewModel.kt           # Injects UseCases; maps domain→data for UI
└── XxxUiEvent.kt

states/
└── XxxUiState.kt
```

---

## Rules

1. **ViewModel** → UseCases only. No repository injection.
2. **UseCase** → Domain repository interfaces; works with domain models only.
3. **Repository interface** → In `domain/repository/`; implementations in `data/repository/` (flavor-specific).
4. **Domain models** → Pure Kotlin (no Android/Firebase types). Data models for Parcelable/JSON/Firestore.
5. **UI** → Receives data models for display (datedmy, time, Gson). ViewModels convert domain → data at boundary.
