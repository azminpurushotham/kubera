package com.collection.kubera.domain.repository

import android.content.Intent
import com.collection.kubera.domain.model.GoogleAuthResult
import com.collection.kubera.domain.model.Result

interface GoogleAuthRepository {
    fun getSignInIntent(webClientId: String): Intent?
    suspend fun signInWithCredential(idToken: String): Result<GoogleAuthResult>
}
