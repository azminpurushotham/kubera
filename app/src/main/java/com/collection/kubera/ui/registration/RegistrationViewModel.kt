package com.collection.kubera.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.states.RegistrationUiState
import com.collection.kubera.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
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

    fun getUsers(){
        Timber.v("getUsers")
        _uiState.value = RegistrationUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("user")
                .orderBy("username",Query.Direction.ASCENDING)
                .get().await()
            _users.value = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)?.apply { id = it.id }
            }
            _uiState.value = RegistrationUiState.RegistrationSuccess("Success")
            _uiState.value = RegistrationUiState.Initial
        }
    }

    fun login(userName: String, password: String) {
        Timber.v("login")
        _uiState.value = RegistrationUiState.Loading

        users.value.forEach {
            if( it.username == userName && it.password == password){
                Timber.tag("USER1").v("userName -> ${it.username} password -> ${it.password}")
                Timber.tag("USER2").v("userName -> $userName password -> $password")
            } else {
                Timber.tag("USER-1").v("userName -> ${it.username} password -> ${it.password}")
                Timber.tag("USER-2").v("userName -> $userName password -> $password")
            }
        }

//        users.value.indexOfFirst {
////            Timber.tag("USER1").v("userName -> ${it.username} password -> ${it.password}")
////            Timber.tag("USER2").v("userName -> $userName password -> $password")
//            if( it.username.equals(userName) && it.password .equals(password)){
//                return@indexOfFirst  true
//            } else {
//                return@indexOfFirst  false
//            }
//            }.also { result->
//                result.takeIf { result >= 0 }?.let {
//                    _uiState.value = RegistrationUiState.RegistrationSuccess("Successfully logged in")
//                } ?: run {
//                    _uiState.value = RegistrationUiState.RegistrationError("Please enter correct credentials")
//                }
//            }
//        users.value.indexOfLast{
//            Timber.tag("USER10").v("userName -> ${it.username} password -> ${it.password}")
//            Timber.tag("USER20").v("userName -> $userName password -> $password")
//            it.username.equals(userName) && it.password.equals(password)}.also {
//            it.takeIf { it >= 0 }?.let {
//                _uiState.value = RegistrationUiState.RegistrationSuccess("Successfully logged in")
//            } ?: run {
//                _uiState.value = RegistrationUiState.RegistrationError("Please enter correct credentials")
//            }
//        }
    }
}