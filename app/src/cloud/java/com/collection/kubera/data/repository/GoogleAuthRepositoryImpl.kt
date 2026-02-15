package com.collection.kubera.data.repository

import android.content.Intent
import com.collection.kubera.data.Result

/**
 * No-op implementation for cloud flavor.
 * Google Sign-In is only enabled in genaral flavor.
 */
class GoogleAuthRepositoryImpl : GoogleAuthRepository {

    override fun getSignInIntent(webClientId: String): Intent? = null

    override suspend fun signInWithCredential(idToken: String): Result<GoogleAuthResult> =
        Result.Error(Exception("Google Sign-In is not available in cloud mode"))
}
