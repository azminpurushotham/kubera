package com.collection.kubera.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.states.LoginUiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> =
        MutableStateFlow(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()
    private val firestore = FirebaseFirestore.getInstance()

    fun login(userName: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.v("login")
            _uiState.value = LoginUiState.Loading
            firestore.collection("user")
                .whereEqualTo("username", userName)
                .whereEqualTo("password", password)
                .get().addOnSuccessListener {  querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            Timber.v("Document found: ${document.id}")
                            _uiState.value = LoginUiState.LoginSuccess("Successfully logged in")
                        }
                    } else {
                        Timber.v("No matching documents found.")
                        _uiState.value = LoginUiState.LoginFiled("Please enter correct credentials")
                    }
                }.addOnFailureListener {
                    _uiState.value = LoginUiState.LoginFiled("Please enter correct credentials")
                }
        }
    }
}