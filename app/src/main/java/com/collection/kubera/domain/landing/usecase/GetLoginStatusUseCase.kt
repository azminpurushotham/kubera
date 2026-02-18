package com.collection.kubera.domain.landing.usecase

import com.collection.kubera.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use case for checking if user is logged in.
 * Clean Architecture: ViewModel → UseCase → Repository
 */
class GetLoginStatusUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Boolean = userPreferencesRepository.isLoggedIn()
}
