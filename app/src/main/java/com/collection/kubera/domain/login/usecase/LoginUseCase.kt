package com.collection.kubera.domain.login.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.repository.UserPreferencesRepository
import com.collection.kubera.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for login with username/password.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(userName: String, password: String): Result<String?> {
        return when (val result = userRepository.login(userName, password)) {
            is Result.Success -> {
                result.data?.let { userId ->
                    userPreferencesRepository.saveLoginState(userId, userName, password)
                    Result.Success(userId)
                } ?: Result.Error(Exception("Invalid credentials"))
            }
            is Result.Error -> result
        }
    }
}
