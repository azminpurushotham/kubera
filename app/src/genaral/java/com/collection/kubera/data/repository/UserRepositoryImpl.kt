package com.collection.kubera.data.repository

import com.collection.kubera.data.local.dao.UserDao
import com.collection.kubera.data.mapper.toDomainUser
import com.collection.kubera.domain.model.Result
import com.collection.kubera.domain.model.User
import com.collection.kubera.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getAllUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val entities = userDao.getAll()
            Result.Success(entities.map { it.toDomainUser() })
        } catch (e: Exception) {
            Timber.e(e, "getAllUsers failed")
            Result.Error(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<String?> = withContext(Dispatchers.IO) {
        Timber.d("UserRepositoryImpl(genaral): login username=$username")
        try {
            val user = userDao.login(username, password).firstOrNull()
            Timber.d("UserRepositoryImpl(genaral): login query result userId=${user?.id ?: "null"}")
            Result.Success(user?.id)
        } catch (e: Exception) {
            Timber.e(e, "UserRepositoryImpl(genaral): login failed")
            Result.Error(e)
        }
    }

    override suspend fun getUserById(id: String): Result<User?> = withContext(Dispatchers.IO) {
        try {
            val entity = userDao.getById(id).firstOrNull()
            Result.Success(entity?.toDomainUser())
        } catch (e: Exception) {
            Timber.e(e, "getUserById failed")
            Result.Error(e)
        }
    }

    override suspend fun updateUserCredentials(
        userId: String,
        username: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.updateCredentials(userId, username, password)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateUserCredentials failed")
            Result.Error(e)
        }
    }
}
