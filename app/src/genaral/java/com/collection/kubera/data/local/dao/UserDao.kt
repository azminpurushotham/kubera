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
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getById(id: String): List<UserEntity>

    @Query("SELECT * FROM user WHERE username = :username AND password = :password LIMIT 1")
    fun login(username: String, password: String): List<UserEntity>

    @Query("UPDATE user SET username = :username, password = :password WHERE id = :userId")
    fun updateCredentials(userId: String, username: String, password: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: UserEntity)
}
