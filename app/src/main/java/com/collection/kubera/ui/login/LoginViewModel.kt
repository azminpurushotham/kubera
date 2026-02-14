package com.collection.kubera.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.repository.UserPreferencesRepository
import com.collection.kubera.data.repository.UserRepository
import com.collection.kubera.states.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    fun login(userName: String, password: String) {
        Timber.d("login")
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch(dispatcher) {
            when (val result = userRepository.login(userName, password)) {
                is Result.Success -> {
                    result.data?.let { userId ->
                        userPreferencesRepository.saveLoginState(userId, userName, password)
                        _uiEvent.tryEmit(LoginUiEvent.ShowSuccess(RepositoryConstants.LOGIN_SUCCESS_MESSAGE))
                        _uiEvent.tryEmit(LoginUiEvent.NavigateToMain)
                    } ?: run {
                        _uiEvent.tryEmit(
                            LoginUiEvent.ShowError(RepositoryConstants.LOGIN_CREDENTIALS_ERROR)
                        )
                    }
                }
                is Result.Error -> {
                    _uiEvent.tryEmit(
                        LoginUiEvent.ShowError(
                            result.exception.message
                                ?: RepositoryConstants.LOGIN_CREDENTIALS_ERROR
                        )
                    )
                }
            }
            _uiState.value = LoginUiState.Initial
        }
    }
}
