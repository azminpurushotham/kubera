package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.USER_COLLECTION
import com.collection.kubera.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface UserRepository {
    suspend fun getUserById(id: String): Result<User?>
    suspend fun updateUserCredentials(userId: String, username: String, password: String): Result<Unit>
}

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    private val userCollection
        get() = firestore.collection(USER_COLLECTION)

    override suspend fun getUserById(id: String): Result<User?> {
        return try {
            Timber.d("getUserById")
            val doc = userCollection.document(id).get().await()
            val user = doc.toObject(User::class.java)?.apply { this.id = id }
            if (doc.data?.isNotEmpty() == true && user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("No matching documents found"))
            }
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
            Timber.d("updateUserCredentials")
            userCollection.document(userId)
                .update(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateUserCredentials failed")
            Result.Error(e)
        }
    }
}
