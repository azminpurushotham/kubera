package com.collection.kubera.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop")
data class ShopEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "shopName")
    val shopName: String = "",
    @ColumnInfo(name = "s_shopName")
    val sShopName: String = "",
    @ColumnInfo(name = "location")
    val location: String = "",
    @ColumnInfo(name = "landmark")
    val landmark: String? = "",
    @ColumnInfo(name = "firstName")
    val firstName: String = "",
    @ColumnInfo(name = "s_firstName")
    val sFirstName: String = "",
    @ColumnInfo(name = "lastName")
    val lastName: String = "",
    @ColumnInfo(name = "s_lastName")
    val sLastName: String = "",
    @ColumnInfo(name = "phoneNumber")
    val phoneNumber: String? = null,
    @ColumnInfo(name = "secondPhoneNumber")
    val secondPhoneNumber: String? = null,
    @ColumnInfo(name = "mailId")
    val mailId: String = "",
    @ColumnInfo(name = "balance")
    val balance: Long? = null,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = 0L,
    @ColumnInfo(name = "status")
    val status: Boolean = true,
)
