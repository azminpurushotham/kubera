package com.collection.kubera.ui.landing

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.collection.kubera.states.UiState
import com.collection.kubera.utils.ISLOGGEDIN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class LandingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()


    fun checkLoginStatus(pref: SharedPreferences): Boolean {
        Timber.i("checkLoginStatus")
       return pref.getBoolean(ISLOGGEDIN, false)
    }
}