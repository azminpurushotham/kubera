package com.collection.kubera.ui.settings
import androidx.lifecycle.ViewModel
import com.collection.kubera.states.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsUiState> =
        MutableStateFlow(SettingsUiState.Initial)
    val uiState: MutableStateFlow<SettingsUiState> = _uiState
}