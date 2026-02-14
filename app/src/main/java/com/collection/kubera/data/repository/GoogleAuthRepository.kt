package com.collection.kubera.data.repository

import android.content.Intent
import com.collection.kubera.data.Result

/**
 * Repository for Firebase Auth with Google Sign-In.
 * Only used when Google Sign-In is selected; works with cloud flavor (Firebase).
 */
interface GoogleAuthRepository {
    fun getSignInIntent(webClientId: String): Intent?
    suspend fun signInWithCredential(idToken: String): Result<GoogleAuthResult>
}

data class GoogleAuthResult(
    val userId: String,
    val displayName: String,
    val email: String?
)
