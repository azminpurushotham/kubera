package com.collection.kubera.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance")
data class BalanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "balance")
    val balance: Long = 0L,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0L,
)
