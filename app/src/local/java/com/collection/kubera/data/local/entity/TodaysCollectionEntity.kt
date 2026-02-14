package com.collection.kubera.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todays_collection")
data class TodaysCollectionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "balance")
    val balance: Long = 0L,
    @ColumnInfo(name = "credit")
    val credit: Long = 0L,
    @ColumnInfo(name = "debit")
    val debit: Long = 0L,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0L,
)
