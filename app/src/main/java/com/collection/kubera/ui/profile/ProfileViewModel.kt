package com.collection.kubera.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.USER_COLLECTION
import com.collection.kubera.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileUiState> =
        MutableStateFlow(ProfileUiState.Initial)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    val firestore = FirebaseFirestore.getInstance()
    private lateinit var userCredentials: User

    init {
        Timber.i("init")
    }

    fun getUserDetails(id:String) {
        firestore
            .collection(USER_COLLECTION)
            .document(id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.data?.isNotEmpty() == true) {
                    querySnapshot.toObject(User::class.java)?.
                    apply {
                        this.id = id
                    }?.let {
                        userCredentials = it
                    }
                    _uiState.value = ProfileUiState.UserCredentials(userCredentials)
                } else {
                    Timber.i("No matching documents found.")
                    _uiState.value =
                        ProfileUiState.Error("Something went wrong with this user details")
                }
            }
            .addOnFailureListener {
                _uiState.value =
                    ProfileUiState.Error("Something went wrong with this user details,$it")
            }
    }

    fun updateCredentials(
        userName: String,
        password: String,
        confirmPassword: String,
    ) {
        if (userName.isEmpty()) {
            _uiState.value = ProfileUiState.UserNameError("Username cannot be empty")
            return
        }
        if (password.isEmpty()) {
            _uiState.value = ProfileUiState.PasswordError("Password cannot be empty")
        }
        if (password != confirmPassword) {
            _uiState.value =
                ProfileUiState.PasswordMismatchError("Passwords do not matching")
            return
        }

        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(USER_COLLECTION)
                .document(userCredentials.id)
                .update("username", userName,
                    "password", password)
                .addOnSuccessListener {
                    Timber.tag("userCredentials").v(Gson().toJson(userCredentials))
                    _uiState.value =
                        ProfileUiState.UpdationSuccess("Credentials updated successfully")
                }
                .addOnFailureListener { exception ->
                    Timber.tag("exception").e(exception)
                    Timber.tag("userCredentials").v(Gson().toJson(userCredentials))
                    _uiState.value =
                        ProfileUiState.UpdationFiled("Failed to update credentials\n ${exception.message}")
                }
        }
    }

    fun setPasswordMismatchError() {
        _uiState.value =
            ProfileUiState.PasswordMismatchError("Passwords do not matching")
    }
}