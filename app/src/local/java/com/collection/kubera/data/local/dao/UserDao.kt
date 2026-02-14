package com.collection.kubera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.collection.kubera.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY username ASC")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM user WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?

    @Query("UPDATE user SET username = :username, password = :password WHERE id = :userId")
    suspend fun updateCredentials(userId: String, username: String, password: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserEntity)
}
