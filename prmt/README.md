# Prompts (prmt)

**Clean Architecture:** ViewModel → UseCase → Repository

Reference: **`ui/shoplist/`**, **`domain/shoplist/usecase/`**

Prompts for standardizing feature directories (addnewshop, shopdetails, orderhistory, profile, etc.).

| # | File | Purpose |
|---|------|---------|
| 01 | [architecture-overview.md](01-architecture-overview.md) | Project structure & dependency flow |
| 02 | [repository-prompt.md](02-repository-prompt.md) | Data layer & repository pattern |
| 02a | [usecase-prompt.md](02a-usecase-prompt.md) | Domain layer & use case pattern |
| 03 | [hilt-di-prompt.md](03-hilt-di-prompt.md) | Dependency injection with Hilt |
| 04 | [viewmodel-prompt.md](04-viewmodel-prompt.md) | ViewModel (use cases only) |
| 05 | [ui-events-prompt.md](05-ui-events-prompt.md) | One-time events (SharedFlow, emit) |
| 06 | [screen-composable-prompt.md](06-screen-composable-prompt.md) | Compose screen pattern |
| 07 | [state-design-prompt.md](07-state-design-prompt.md) | UI state design |
| 08 | [constants-prompt.md](08-constants-prompt.md) | Constants & configuration |
| 09 | [migration-checklist.md](09-migration-checklist.md) | Checklist for migrating features |
