package com.collection.kubera.data.mapper

import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Shop
import com.collection.kubera.data.User
import com.collection.kubera.domain.model.CollectionModel as DomainCollectionModel
import com.collection.kubera.domain.model.Shop as DomainShop
import com.collection.kubera.domain.model.User as DomainUser
import com.google.firebase.Timestamp

/**
 * Converts data models (Firestore/Parcelable) to domain models. Shared across flavors.
 * Entity-to-domain mappers are in genaral: EntityToDomainMappers.kt
 */
fun Shop.toDomainShop(): DomainShop = DomainShop(
    id = id,
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
    timestampMillis = timestamp?.toDate()?.time ?: 0L,
    status = status,
)

fun User.toDomainUser(): DomainUser = DomainUser(
    id = id,
    username = username,
    password = password,
    status = status,
    loggedInTimeMillis = loggedintime?.toDate()?.time ?: 0L,
)

fun CollectionModel.toDomainCollectionModel(): DomainCollectionModel = DomainCollectionModel(
    id = id,
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
    timestampMillis = timestamp?.toDate()?.time ?: 0L,
)
