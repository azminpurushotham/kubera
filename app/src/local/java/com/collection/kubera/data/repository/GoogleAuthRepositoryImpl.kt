package com.collection.kubera.data.repository

import android.content.Intent
import com.collection.kubera.data.Result

/**
 * No-op implementation for local flavor.
 * Google Sign-In requires Firebase Auth, which is only used in cloud flavor.
 */
class GoogleAuthRepositoryImpl : GoogleAuthRepository {

    override fun getSignInIntent(webClientId: String): Intent? = null

    override suspend fun signInWithCredential(idToken: String): Result<GoogleAuthResult> =
        Result.Error(Exception("Google Sign-In is not available in local mode"))
}
