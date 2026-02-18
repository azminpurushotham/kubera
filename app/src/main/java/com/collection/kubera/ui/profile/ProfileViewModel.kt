package com.collection.kubera.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.User
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.profile.usecase.GetCurrentUserIdUseCase
import com.collection.kubera.domain.profile.usecase.GetUserByIdUseCase
import com.collection.kubera.domain.updatecredentials.usecase.UpdateUserCredentialsUseCase
import com.collection.kubera.states.ProfileUiState
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
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserCredentialsUseCase: UpdateUserCredentialsUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    private var userCredentials: User? = null

    fun init() {
        Timber.d("init")
        val userId = getCurrentUserIdUseCase()
        if (userId.isNotEmpty()) {
            getUserDetails(userId)
        } else {
            viewModelScope.launch(dispatcher) {
                _uiEvent.emit(
                    ProfileUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR)
                )
            }
        }
    }

    fun getUserDetails(id: String) {
        viewModelScope.launch(dispatcher) {
            when (val result = getUserByIdUseCase(id)) {
                is Result.Success -> {
                    result.data?.let { user ->
                        userCredentials = user
                        _uiState.value = ProfileUiState.UserCredentials(user)
                    } ?: run {
                        _uiEvent.emit(
                            ProfileUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR)
                        )
                    }
                }
                is Result.Error -> {
                    _uiEvent.emit(
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
            viewModelScope.launch(dispatcher) {
                _uiEvent.emit(ProfileUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR))
            }
            return
        }

        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch(dispatcher) {
            when (val result = updateUserCredentialsUseCase(user.id, userName, password)) {
                is Result.Success -> {
                    _uiEvent.emit(
                        ProfileUiEvent.ShowSuccess(RepositoryConstants.PROFILE_CREDENTIALS_UPDATED)
                    )
                }
                is Result.Error -> {
                    _uiEvent.emit(
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
