package com.collection.kubera.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<LandingUiEvent>()
    val uiEvent: SharedFlow<LandingUiEvent> = _uiEvent.asSharedFlow()

    fun init() {
        Timber.d("init - checkLoginStatus")
        viewModelScope.launch(dispatcher) {
            val isLoggedIn = userPreferencesRepository.isLoggedIn()
            Timber.d("isLoggedIn -> $isLoggedIn")
            if (isLoggedIn) {
                _uiEvent.emit(LandingUiEvent.NavigateToMain)
            } else {
                _uiEvent.emit(LandingUiEvent.NavigateToLogin)
            }
        }
    }
}
