package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.User

interface UserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(id: String): Result<User?>
    suspend fun login(username: String, password: String): Result<String?>
    suspend fun updateUserCredentials(userId: String, username: String, password: String): Result<Unit>
}
