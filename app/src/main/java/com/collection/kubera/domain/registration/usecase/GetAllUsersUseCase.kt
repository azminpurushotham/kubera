package com.collection.kubera.domain.registration.usecase

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.User
import com.collection.kubera.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for loading all users.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> = userRepository.getAllUsers()
}
