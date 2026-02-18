package com.collection.kubera.domain.login.usecase

import android.content.Intent
import com.collection.kubera.domain.repository.GoogleAuthRepository
import javax.inject.Inject

/**
 * Use case for obtaining Google Sign-In intent.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetGoogleSignInIntentUseCase @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository
) {
    operator fun invoke(webClientId: String): Intent? =
        googleAuthRepository.getSignInIntent(webClientId)
}
