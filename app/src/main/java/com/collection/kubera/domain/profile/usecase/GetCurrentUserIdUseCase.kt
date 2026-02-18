package com.collection.kubera.domain.profile.usecase

import com.collection.kubera.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use case for getting current logged-in user ID.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetCurrentUserIdUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): String = userPreferencesRepository.getUserId()
}
