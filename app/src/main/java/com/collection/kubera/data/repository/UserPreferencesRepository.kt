package com.collection.kubera.data.repository

interface UserPreferencesRepository {
    fun getUserId(): String
    fun getUserName(): String
    fun isLoggedIn(): Boolean
    fun saveLoginState(userId: String, userName: String, password: String)
}
