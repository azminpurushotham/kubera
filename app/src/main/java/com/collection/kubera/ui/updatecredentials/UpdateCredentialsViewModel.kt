package com.collection.kubera.ui.updatecredentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.USER_COLLECTION
import com.collection.kubera.data.User
import com.collection.kubera.states.UpdateCredentialsUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class UpdateCredentialsViewModel(val userCredentials: User) : ViewModel() {
    private val _uiState: MutableStateFlow<UpdateCredentialsUiState> =
        MutableStateFlow(UpdateCredentialsUiState.Initial)
    val uiState: StateFlow<UpdateCredentialsUiState> = _uiState.asStateFlow()
    val firestore = FirebaseFirestore.getInstance()

    init {
        Timber.i("init")
        _uiState.value =  UpdateCredentialsUiState.UserCredentials(userCredentials)
    }

    fun updateCredentials(
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        if (userName.isEmpty()) {
            _uiState.value = UpdateCredentialsUiState.UserNameError("Username cannot be empty")
            return
        }
        if (password.isEmpty()) {
            _uiState.value = UpdateCredentialsUiState.PasswordError("Password cannot be empty")
        }
        if (password != confirmPassword) {
            _uiState.value =
                UpdateCredentialsUiState.PasswordMismatchError("Passwords do not matching")
            return
        }

        _uiState.value = UpdateCredentialsUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(USER_COLLECTION).document(userCredentials.id)
                .update("username", userName, "password", password)
                .addOnSuccessListener {
                    Timber.tag("userCredentials").v(Gson().toJson(userCredentials))
                    _uiState.value =
                        UpdateCredentialsUiState.UpdationSuccess("Credentials updated successfully")
                }
                .addOnFailureListener { exception ->
                    Timber.tag("exception").e(exception)
                    Timber.tag("userCredentials").v(Gson().toJson(userCredentials))
                    _uiState.value =
                        UpdateCredentialsUiState.UpdationFiled("Failed to update credentials\n ${exception.message}")
                }
        }
    }
}