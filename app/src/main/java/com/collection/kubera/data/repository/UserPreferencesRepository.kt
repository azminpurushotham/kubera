package com.collection.kubera.data.repository

interface UserPreferencesRepository {
    fun getUserId(): String
    fun getUserName(): String
    fun saveLoginState(userId: String, userName: String, password: String)
}
