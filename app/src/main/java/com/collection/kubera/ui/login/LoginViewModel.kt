package com.collection.kubera.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.states.LoginUiState
import com.collection.kubera.utils.ISLOGGEDIN
import com.collection.kubera.utils.PASSWORD
import com.collection.kubera.utils.PreferenceHelper.set
import com.collection.kubera.utils.USER_ID
import com.collection.kubera.utils.USER_NAME
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
    var pref : SharedPreferences? = null

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
                            pref?.set(USER_NAME,userName)
                            pref?.set(PASSWORD,password)
                            pref?.set(ISLOGGEDIN,true)
                            pref?.set(USER_ID,document.id)
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

    fun setPreference(pref: SharedPreferences) {
        this.pref = pref
    }
}