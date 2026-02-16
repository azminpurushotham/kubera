package com.collection.kubera.data.repository

import com.collection.kubera.data.Result
import com.collection.kubera.data.USER_COLLECTION
import com.collection.kubera.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    private val userCollection get() = firestore.collection(USER_COLLECTION)

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = userCollection
                .orderBy("username", Query.Direction.ASCENDING)
                .get().await()
            val users = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)?.apply { id = it.id }
            }
            Result.Success(users)
        } catch (e: Exception) {
            Timber.e(e, "getAllUsers failed")
            Result.Error(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<String?> {
        Timber.d("UserRepositoryImpl(cloud): login username=$username Firestore query ${USER_COLLECTION}")
        return try {
            val querySnapshot = userCollection
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get().await()
            val userId = querySnapshot.documents.firstOrNull()?.id
            Timber.d("UserRepositoryImpl(cloud): login Firestore result docCount=${querySnapshot.size()} userId=$userId")
            Result.Success(userId)
        } catch (e: Exception) {
            Timber.e(e, "UserRepositoryImpl(cloud): login failed")
            Result.Error(e)
        }
    }

    override suspend fun getUserById(id: String): Result<User?> {
        return try {
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
            userCollection.document(userId)
                .update(mapOf("username" to username, "password" to password))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "updateUserCredentials failed")
            Result.Error(e)
        }
    }
}
