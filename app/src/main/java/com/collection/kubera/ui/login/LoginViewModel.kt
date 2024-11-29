package com.collection.kubera.ui.login

import androidx.lifecycle.ViewModel
import com.collection.kubera.states.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

}