package com.collection.kubera.domain.profile.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.User
import com.collection.kubera.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for loading user by ID.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String): Result<User?> = userRepository.getUserById(id)
}
