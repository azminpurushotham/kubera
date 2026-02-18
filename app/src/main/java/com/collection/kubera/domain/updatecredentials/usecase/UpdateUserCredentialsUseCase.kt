package com.collection.kubera.domain.updatecredentials.usecase

import com.collection.kubera.data.Result
import com.collection.kubera.data.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for updating user credentials.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class UpdateUserCredentialsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, userName: String, password: String): Result<Unit> =
        userRepository.updateUserCredentials(userId, userName, password)
}
