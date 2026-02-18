package com.collection.kubera.data.mapper

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Shop
import com.collection.kubera.data.User
import com.collection.kubera.domain.model.CollectionModel as DomainCollectionModel
import com.collection.kubera.domain.model.Shop as DomainShop
import com.collection.kubera.domain.model.User as DomainUser
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Converts domain models to data layer types (for Parcelable, JSON, navigation).
 */
fun DomainUser.toDataUser(): User = User(
    id = id,
    username = username,
    password = password,
    status = status,
    loggedintime = if (loggedInTimeMillis > 0) Timestamp(Date(loggedInTimeMillis)) else null,
)

fun DomainShop.toDataShop(): Shop = Shop(
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
    timestamp = if (timestampMillis > 0) Timestamp(Date(timestampMillis)) else null,
    status = status,
)

fun DomainCollectionModel.toDataCollectionModel(): CollectionModel = CollectionModel(
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
    timestamp = if (timestampMillis > 0) Timestamp(Date(timestampMillis)) else null,
)
