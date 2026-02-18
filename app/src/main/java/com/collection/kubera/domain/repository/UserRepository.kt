package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.User

interface UserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(id: String): Result<User?>
    suspend fun login(username: String, password: String): Result<String?>
    suspend fun updateUserCredentials(userId: String, username: String, password: String): Result<Unit>
}
