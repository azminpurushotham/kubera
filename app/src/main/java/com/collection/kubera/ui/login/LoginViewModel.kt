package com.collection.kubera.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.repository.GoogleAuthRepository
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
    private val googleAuthRepository: GoogleAuthRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    fun login(userName: String, password: String) {
        Timber.d("login() called userName=$userName")
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch(dispatcher) {
            Timber.d("login: calling userRepository.login")
            when (val result = userRepository.login(userName, password)) {
                is Result.Success -> {
                    Timber.d("login: userRepository.login Success userId=${result.data ?: "null"}")
                    result.data?.let { userId ->
                        userPreferencesRepository.saveLoginState(userId, userName, password)
                        Timber.d("login: saved login state, emitting success and navigate")
                        _uiEvent.emit(LoginUiEvent.ShowSuccess(RepositoryConstants.LOGIN_SUCCESS_MESSAGE))
                        _uiEvent.emit(LoginUiEvent.NavigateToMain)
                    } ?: run {
                        Timber.w("login: userId is null, credentials invalid")
                        _uiEvent.emit(LoginUiEvent.ShowError(RepositoryConstants.LOGIN_CREDENTIALS_ERROR))
                    }
                }
                is Result.Error -> {
                    Timber.e(result.exception, "login: userRepository.login Error")
                    _uiEvent.emit(
                        LoginUiEvent.ShowError(
                            result.exception.message
                                ?: RepositoryConstants.LOGIN_CREDENTIALS_ERROR
                        )
                    )
                }
            }
            Timber.d("login: completed, resetting uiState")
            _uiState.value = LoginUiState.Initial
        }
    }

    fun getGoogleSignInIntent(webClientId: String): Intent? =
        googleAuthRepository.getSignInIntent(webClientId)

    fun showError(message: String) {
        viewModelScope.launch { _uiEvent.emit(LoginUiEvent.ShowError(message)) }
    }

    fun signInWithGoogle(idToken: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch(dispatcher) {
            when (val result = googleAuthRepository.signInWithCredential(idToken)) {
                is Result.Success -> {
                    val data = result.data
                    userPreferencesRepository.saveLoginState(
                        userId = data.userId,
                        userName = data.displayName,
                        password = ""
                    )
                    _uiEvent.emit(LoginUiEvent.ShowSuccess("Signed in with Google"))
                    _uiEvent.emit(LoginUiEvent.NavigateToMain)
                }
                is Result.Error -> {
                    _uiEvent.emit(
                        LoginUiEvent.ShowError(
                            result.exception.message ?: "Google sign-in failed"
                        )
                    )
                }
            }
            _uiState.value = LoginUiState.Initial
        }
    }
}
