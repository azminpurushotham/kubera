package com.collection.kubera.ui.updatecredentials

import androidx.lifecycle.ViewModel
import com.collection.kubera.states.UpdateCredentialsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpdateCredentialsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UpdateCredentialsUiState> =
        MutableStateFlow(UpdateCredentialsUiState.Initial)
    val uiState: StateFlow<UpdateCredentialsUiState> =
        _uiState.asStateFlow()

    fun updateCredentials(userName: String, password: String, confirmPassword: String) {
        if (userName.isEmpty()){
            _uiState.value = UpdateCredentialsUiState.UserNameError("Username cannot be empty")
            return
        }
        if (password.isEmpty()){
            _uiState.value = UpdateCredentialsUiState.PasswordError("Password cannot be empty")
        }
       if(password != confirmPassword){
           _uiState.value = UpdateCredentialsUiState.PasswordMismatchError("Passwords do not matching")
           return
       }
    }

}