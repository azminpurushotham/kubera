# State Design Prompt

Use when defining UI state.

---

> - Prefer combined state objects over multiple StateFlows (e.g. `TodaysCollectionData(balance, credit, debit)` instead of 3 separate flows)
> - Use sealed interface for uiState: `Initial`, `Loading`, `Success`, `Error`
> - Keep state minimal - derive UI from state, don't store computed values

---

**Reference:** `data/TodaysCollectionData.kt`, `states/HomeUiState.kt`
