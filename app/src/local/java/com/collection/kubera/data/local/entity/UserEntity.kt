package com.collection.kubera.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "username")
    val username: String = "",
    @ColumnInfo(name = "password")
    val password: String = "",
    @ColumnInfo(name = "status")
    val status: Boolean = false,
    @ColumnInfo(name = "loggedintime")
    val loggedintime: Long = 0L,
)
