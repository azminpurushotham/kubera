package com.collection.kubera.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.User
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.data.repository.UserPreferencesRepository
import com.collection.kubera.data.repository.UserRepository
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    private var userCredentials: User? = null

    fun init() {
        Timber.d("init")
        val userId = userPreferencesRepository.getUserId()
        if (userId.isNotEmpty()) {
            getUserDetails(userId)
        } else {
            _uiEvent.tryEmit(
                ProfileUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR)
            )
        }
    }

    fun getUserDetails(id: String) {
        viewModelScope.launch(dispatcher) {
            when (val result = userRepository.getUserById(id)) {
                is Result.Success -> {
                    result.data?.let { user ->
                        userCredentials = user
                        _uiState.value = ProfileUiState.UserCredentials(user)
                    } ?: run {
                        _uiEvent.tryEmit(
                            ProfileUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR)
                        )
                    }
                }
                is Result.Error -> {
                    _uiEvent.tryEmit(
                        ProfileUiEvent.ShowError(
                            result.exception.message
                                ?: RepositoryConstants.PROFILE_USER_DETAILS_ERROR
                        )
                    )
                }
            }
        }
    }

    fun updateCredentials(
        userName: String,
        password: String,
        confirmPassword: String,
    ) {
        when {
            userName.isEmpty() -> {
                _uiState.value = ProfileUiState.UserNameError(RepositoryConstants.PROFILE_USERNAME_EMPTY)
                return
            }
            password.isEmpty() -> {
                _uiState.value = ProfileUiState.PasswordError(RepositoryConstants.PROFILE_PASSWORD_EMPTY)
                return
            }
            password != confirmPassword -> {
                _uiState.value = ProfileUiState.PasswordMismatchError(RepositoryConstants.PROFILE_PASSWORD_MISMATCH)
                return
            }
        }

        val user = userCredentials
        if (user == null) {
            _uiEvent.tryEmit(ProfileUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR))
            return
        }

        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch(dispatcher) {
            when (val result = userRepository.updateUserCredentials(user.id, userName, password)) {
                is Result.Success -> {
                    _uiEvent.tryEmit(
                        ProfileUiEvent.ShowSuccess(RepositoryConstants.PROFILE_CREDENTIALS_UPDATED)
                    )
                }
                is Result.Error -> {
                    _uiEvent.tryEmit(
                        ProfileUiEvent.ShowError(
                            "${RepositoryConstants.PROFILE_UPDATE_FAILED}\n${result.exception.message}"
                        )
                    )
                }
            }
            _uiState.value = ProfileUiState.UserCredentials(user.copy(username = userName, password = password))
        }
    }

    fun setPasswordMismatchError() {
        _uiState.value = ProfileUiState.PasswordMismatchError(RepositoryConstants.PROFILE_PASSWORD_MISMATCH)
    }
}
