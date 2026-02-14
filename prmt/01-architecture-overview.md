# Architecture Overview

Reference: **`ui/shoplist/`**

```
data/
├── repository/           # Interface + Impl, no ViewModel/Firestore in UI
│   ├── XxxRepository.kt
│   └── XxxRepositoryImpl.kt
├── XxxData.kt            # Domain models (not platform types like DocumentSnapshot)
└── ...

di/
├── XxxModule.kt          # Hilt module per feature
└── ...

ui/[feature]/
├── XxxScreen.kt          # Composable, uses hiltViewModel()
├── XxxViewModel.kt       # @HiltViewModel, @Inject constructor
├── XxxUiEvent.kt         # One-time events (SharedFlow)
├── XxxUiState.kt         # Screen-specific state (optional)
└── ...
```
