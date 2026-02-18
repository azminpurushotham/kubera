package com.collection.kubera.data.repository

import android.content.Intent
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.GoogleAuthRepository

/**
 * No-op implementation for cloud flavor.
 * Google Sign-In is only enabled in genaral flavor.
 */
class GoogleAuthRepositoryImpl : GoogleAuthRepository {

    override fun getSignInIntent(webClientId: String): Intent? = null

    override suspend fun signInWithCredential(idToken: String): Result<com.collection.kubera.domain.model.GoogleAuthResult> =
        Result.Error(Exception("Google Sign-In is not available in cloud mode"))
}
