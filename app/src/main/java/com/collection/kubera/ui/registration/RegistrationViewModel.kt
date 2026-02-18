package com.collection.kubera.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.mapper.toDataUser
import com.collection.kubera.data.repository.RepositoryConstants
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.User
import com.collection.kubera.domain.registration.usecase.GetAllUsersUseCase
import com.collection.kubera.states.RegistrationUiState
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
class RegistrationViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Initial)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RegistrationUiEvent>(replay = 0, extraBufferCapacity = 2)
    val uiEvent: SharedFlow<RegistrationUiEvent> = _uiEvent.asSharedFlow()

    fun init() {
        Timber.d("getUsers")
        _uiState.value = RegistrationUiState.Loading
        viewModelScope.launch(dispatcher) {
            when (val result = getAllUsersUseCase()) {
                is Result.Success -> {
                    _users.value = result.data
                    _uiState.value = RegistrationUiState.Initial
                }
                is Result.Error -> {
                    _uiEvent.emit(
                        RegistrationUiEvent.ShowError(
                            result.exception.message ?: RepositoryConstants.DEFAULT_ERROR_MESSAGE
                        )
                    )
                    _uiState.value = RegistrationUiState.Initial
                }
            }
        }
    }

    fun validateCredentials(userName: String, password: String) {
        Timber.d("validateCredentials")
        _uiState.value = RegistrationUiState.Loading
        viewModelScope.launch(dispatcher) {
            val index = _users.value.indexOfFirst {
                it.username == userName && it.password == password
            }
            if (index >= 0) {
                val user = _users.value[index]
                _uiState.value = RegistrationUiState.Initial
                _uiEvent.emit(RegistrationUiEvent.NavigateToUpdateCredentials(user.toDataUser()))
            } else {
                _uiState.value = RegistrationUiState.Initial
                _uiEvent.emit(
                    RegistrationUiEvent.ShowError(RepositoryConstants.LOGIN_CREDENTIALS_ERROR)
                )
            }
        }
    }
}
