package com.collection.kubera.data.local.mapper

import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TodaysCollectionData
import com.collection.kubera.data.User
import com.google.firebase.Timestamp
import com.collection.kubera.data.local.entity.BalanceEntity
import com.collection.kubera.data.local.entity.CollectionHistoryEntity
import com.collection.kubera.data.local.entity.ShopEntity
import com.collection.kubera.data.local.entity.TodaysCollectionEntity
import com.collection.kubera.data.local.entity.UserEntity
import java.util.Date

fun ShopEntity.toShop(): Shop = Shop(
    id = id,
    shopName = shopName,
    s_shopName = sShopName,
    location = location,
    landmark = landmark,
    firstName = firstName,
    s_firstName = sFirstName,
    lastName = lastName,
    s_lastName = sLastName,
    phoneNumber = phoneNumber,
    secondPhoneNumber = secondPhoneNumber,
    mailId = mailId,
    balance = balance,
    timestamp = if (timestamp > 0) Timestamp(Date(timestamp)) else null,
    status = status,
)

fun Shop.toShopEntity(): ShopEntity = ShopEntity(
    id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
    shopName = shopName,
    sShopName = s_shopName,
    location = location,
    landmark = landmark,
    firstName = firstName,
    sFirstName = s_firstName,
    lastName = lastName,
    sLastName = s_lastName,
    phoneNumber = phoneNumber,
    secondPhoneNumber = secondPhoneNumber,
    mailId = mailId,
    balance = balance,
    timestamp = timestamp?.toDate()?.time ?: System.currentTimeMillis(),
    status = status,
)

fun CollectionHistoryEntity.toCollectionModel(): CollectionModel = CollectionModel(
    id = id,
    shopId = shopId,
    shopName = shopName,
    s_shopName = sShopName,
    firstName = firstName,
    s_firstName = sFirstName,
    lastName = lastName,
    s_lastName = sLastName,
    phoneNumber = phoneNumber,
    secondPhoneNumber = secondPhoneNumber,
    mailId = mailId,
    amount = amount,
    collectedBy = collectedBy,
    collectedById = collectedById,
    transactionType = transactionType,
    timestamp = if (timestamp > 0) Timestamp(Date(timestamp)) else null,
)

fun CollectionModel.toCollectionHistoryEntity(): CollectionHistoryEntity = CollectionHistoryEntity(
    id = id?.takeIf { it.isNotEmpty() } ?: java.util.UUID.randomUUID().toString(),
    shopId = shopId,
    shopName = shopName,
    sShopName = s_shopName,
    firstName = firstName,
    sFirstName = s_firstName,
    lastName = lastName,
    sLastName = s_lastName,
    phoneNumber = phoneNumber,
    secondPhoneNumber = secondPhoneNumber,
    mailId = mailId,
    amount = amount,
    collectedBy = collectedBy,
    collectedById = collectedById,
    transactionType = transactionType,
    timestamp = timestamp?.toDate()?.time ?: System.currentTimeMillis(),
)

fun TodaysCollectionEntity.toTodaysCollectionData(): TodaysCollectionData = TodaysCollectionData(
    balance = balance,
    credit = credit,
    debit = debit,
)

fun UserEntity.toUser(): User = User(
    id = id,
    username = username,
    password = password,
    status = status,
    loggedintime = if (loggedintime > 0) Timestamp(Date(loggedintime)) else null,
)

fun User.toUserEntity(): UserEntity = UserEntity(
    id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
    username = username,
    password = password,
    status = status,
    loggedintime = loggedintime?.toDate()?.time ?: System.currentTimeMillis(),
)

fun BalanceEntity.toBalanceAmount(): BalanceAmount = BalanceAmount(
    balance = balance,
    id = id,
    timestamp = if (timestamp > 0) Timestamp(Date(timestamp)) else Timestamp.now(),
)
