package com.collection.kubera.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.UiState
import com.collection.kubera.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class RegistrationViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users
    val firestore = FirebaseFirestore.getInstance()

    fun getUsers(){
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("user").get().await()
                _users.value = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                _users.value.forEach {
                    Timber.tag("Users").v(it.username)
                }
            } catch (e: Exception) {
                Timber.tag("ERROR").e(e)
            }
        }
    }
}