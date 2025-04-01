package com.collection.kubera.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.User
import com.collection.kubera.states.RegistrationUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class RegistrationViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<RegistrationUiState> =
        MutableStateFlow(RegistrationUiState.Initial)
    val uiState: StateFlow<RegistrationUiState> =
        _uiState.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users
    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers() {
        Timber.i("getUsers")
        _uiState.value = RegistrationUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("user")
                .orderBy("username", Query.Direction.ASCENDING)
                .get().await()
            _users.value = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)?.apply { id = it.id }
            }
            _uiState.value = RegistrationUiState.RegistrationSuccess("Success")
            _uiState.value = RegistrationUiState.Initial
        }
    }

    fun login(userName: String, password: String) {
        Timber.i("login")
        _uiState.value = RegistrationUiState.Loading
        users.value.indexOfFirst { (it.username.equals(userName) && it.password.equals(password)) }
            .also { result ->
                result.takeIf { result >= 0 }?.let {
                    _uiState.value =
                        RegistrationUiState.RegistrationSuccess("Successfully logged in")
                } ?: run {
                    _uiState.value =
                        RegistrationUiState.RegistrationError("Please enter correct credentials")
                }
            }
    }
}