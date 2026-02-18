package com.collection.kubera.ui.updatecredentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Result
import com.collection.kubera.data.User
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.updatecredentials.usecase.UpdateUserCredentialsUseCase
import com.collection.kubera.states.UpdateCredentialsUiState
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
class UpdateCredentialsViewModel @Inject constructor(
    private val updateUserCredentialsUseCase: UpdateUserCredentialsUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateCredentialsUiState>(UpdateCredentialsUiState.Initial)
    val uiState: StateFlow<UpdateCredentialsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UpdateCredentialsUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<UpdateCredentialsUiEvent> = _uiEvent.asSharedFlow()

    private var userCredentials: User? = null

    fun init(user: User) {
        Timber.d("init")
        userCredentials = user
        _uiState.value = UpdateCredentialsUiState.UserCredentials(user)
    }

    fun updateCredentials(
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch(dispatcher) {
            when {
                userName.isEmpty() -> {
                    _uiEvent.emit(
                        UpdateCredentialsUiEvent.ShowError(RepositoryConstants.PROFILE_USERNAME_EMPTY)
                    )
                    return@launch
                }
                password.isEmpty() -> {
                    _uiEvent.emit(
                        UpdateCredentialsUiEvent.ShowError(RepositoryConstants.PROFILE_PASSWORD_EMPTY)
                    )
                    return@launch
                }
                password != confirmPassword -> {
                    _uiEvent.emit(
                        UpdateCredentialsUiEvent.ShowError(RepositoryConstants.PROFILE_PASSWORD_MISMATCH)
                    )
                    return@launch
                }
            }

            val user = userCredentials
            if (user == null) {
                _uiEvent.emit(
                    UpdateCredentialsUiEvent.ShowError(RepositoryConstants.PROFILE_USER_DETAILS_ERROR)
                )
                return@launch
            }

            _uiState.value = UpdateCredentialsUiState.Loading
            when (val result = updateUserCredentialsUseCase(user.id, userName, password)) {
                is Result.Success -> {
                    _uiState.value = UpdateCredentialsUiState.Initial
                    _uiEvent.emit(
                        UpdateCredentialsUiEvent.ShowSuccess(RepositoryConstants.PROFILE_CREDENTIALS_UPDATED)
                    )
                    _uiEvent.emit(UpdateCredentialsUiEvent.NavigateToLogin)
                }
                is Result.Error -> {
                    _uiState.value = UpdateCredentialsUiState.Initial
                    _uiEvent.emit(
                        UpdateCredentialsUiEvent.ShowError(
                            "${RepositoryConstants.PROFILE_UPDATE_FAILED}\n${result.exception.message}"
                        )
                    )
                }
            }
        }
    }
}
