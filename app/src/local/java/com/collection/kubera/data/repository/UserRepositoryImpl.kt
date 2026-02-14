package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.User
import com.collection.kubera.data.local.dao.UserDao
import com.collection.kubera.data.local.mapper.toUser
import com.collection.kubera.data.local.mapper.toUserEntity
import timber.log.Timber
import java.util.UUID

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val entities = userDao.getAll()
            Result.Success(entities.map { it.toUser() })
        } catch (e: Exception) {
            Timber.e(e, "getAllUsers failed")
            Result.Error(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<String?> {
        return try {
            val user = userDao.login(username, password)
            Result.Success(user?.id)
        } catch (e: Exception) {
            Timber.e(e, "login failed")
            Result.Error(e)
        }
    }

    override suspend fun getUserById(id: String): Result<User?> {
        return try {
            val entity = userDao.getById(id)
            Result.Success(entity?.toUser())
        } catch (e: Exception) {
            Timber.e(e, "getUserById failed")
            Result.Error(e)
        }
    }

    override suspend fun updateUserCredentials(
        userId: String,
        username: String,
        password: String
    ): Result<Unit> {
        return try {
            userDao.updateCredentials(userId, username, password)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateUserCredentials failed")
            Result.Error(e)
        }
    }
}
