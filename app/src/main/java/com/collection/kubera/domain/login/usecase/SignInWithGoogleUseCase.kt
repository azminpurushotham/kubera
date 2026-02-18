package com.collection.kubera.domain.login.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.GoogleAuthRepository
import com.collection.kubera.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use case for Google Sign-In.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return when (val result = googleAuthRepository.signInWithCredential(idToken)) {
            is Result.Success -> {
                val data = result.data
                userPreferencesRepository.saveLoginState(
                    userId = data.userId,
                    userName = data.displayName,
                    password = ""
                )
                Result.Success(Unit)
            }
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
