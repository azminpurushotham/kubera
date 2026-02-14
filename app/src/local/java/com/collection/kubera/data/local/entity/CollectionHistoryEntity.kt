package com.collection.kubera.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_history")
data class CollectionHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "shopId")
    val shopId: String? = null,
    @ColumnInfo(name = "shopName")
    val shopName: String? = null,
    @ColumnInfo(name = "s_shopName")
    val sShopName: String? = null,
    @ColumnInfo(name = "firstName")
    val firstName: String? = null,
    @ColumnInfo(name = "s_firstName")
    val sFirstName: String? = null,
    @ColumnInfo(name = "lastName")
    val lastName: String? = null,
    @ColumnInfo(name = "s_lastName")
    val sLastName: String? = null,
    @ColumnInfo(name = "phoneNumber")
    val phoneNumber: String? = null,
    @ColumnInfo(name = "secondPhoneNumber")
    val secondPhoneNumber: String? = null,
    @ColumnInfo(name = "mailId")
    val mailId: String? = null,
    @ColumnInfo(name = "amount")
    val amount: Long? = null,
    @ColumnInfo(name = "collectedBy")
    val collectedBy: String = "Admin",
    @ColumnInfo(name = "collectedById")
    val collectedById: String? = null,
    @ColumnInfo(name = "transactionType")
    val transactionType: String? = null,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0L,
)
