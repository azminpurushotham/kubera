# State Design Prompt

Use when defining UI state.

---

> - Place all UiState sealed types in **`states/`** (shared folder)
> - Use sealed interface for uiState: `Initial`, `Loading`, `Success`, `Error`
> - Prefer combined state objects over multiple StateFlows (e.g. `TodaysCollectionData(balance, credit, debit)`)
> - Keep state minimal — derive UI from state, don't store computed values
> - Validation errors (inline form) may stay in state; success/error toasts go to UiEvent

---

**Reference:** `states/HomeUiState.kt`, `states/LoginUiState.kt`, `data/TodaysCollectionData.kt`
