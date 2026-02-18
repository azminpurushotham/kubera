package com.collection.kubera.data.repository

import android.content.Context
import android.content.Intent
import com.collection.kubera.domain.model.GoogleAuthResult
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.GoogleAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Google Sign-In implementation for genaral flavor.
 * Uses Firebase Auth for authentication.
 */
class GoogleAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GoogleAuthRepository {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    override fun getSignInIntent(webClientId: String): Intent? {
        return try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            val client: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
            client.signInIntent
        } catch (e: Exception) {
            Timber.e(e, "getSignInIntent failed - check default_web_client_id in google-services.json")
            null
        }
    }

    override suspend fun signInWithCredential(idToken: String): Result<GoogleAuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                Result.Success(
                    GoogleAuthResult(
                        userId = user.uid,
                        displayName = user.displayName ?: user.email ?: "User",
                        email = user.email
                    )
                )
            } else {
                Result.Error(Exception("Google sign-in failed"))
            }
        } catch (e: Exception) {
            Timber.e(e, "signInWithCredential failed")
            Result.Error(e)
        }
    }
}
