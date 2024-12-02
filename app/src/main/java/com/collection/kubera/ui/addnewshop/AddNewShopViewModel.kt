package com.collection.kubera.ui.addnewshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.User
import com.collection.kubera.states.HomeUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AddNewShopViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users
    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers() {
        Timber.v("getUsers")
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("user")
                .orderBy("username", Query.Direction.ASCENDING)
                .get().await()
            _users.value = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)?.apply { id = it.id }
            }
            _uiState.value = HomeUiState.HomeSuccess("Success")
            _uiState.value = HomeUiState.Initial
        }
    }

    fun login(userName: String, password: String) {
        Timber.v("login")
        _uiState.value = HomeUiState.Loading
        users.value.indexOfFirst { (it.username.equals(userName) && it.password.equals(password)) }
            .also { result ->
                result.takeIf { result >= 0 }?.let {
                    _uiState.value =
                        HomeUiState.HomeSuccess("Successfully logged in")
                } ?: run {
                    _uiState.value =
                        HomeUiState.HomeError("Please enter correct credentials")
                }
            }
    }
}