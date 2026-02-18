package com.collection.kubera.domain.registration.usecase

import com.collection.kubera.data.Result
import com.collection.kubera.data.User
import com.collection.kubera.data.repository.UserRepository
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
