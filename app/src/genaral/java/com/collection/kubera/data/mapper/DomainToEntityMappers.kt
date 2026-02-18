package com.collection.kubera.data.mapper

import com.collection.kubera.data.local.entity.CollectionHistoryEntity
import com.collection.kubera.data.local.entity.ShopEntity
import com.collection.kubera.data.local.entity.UserEntity
import com.collection.kubera.domain.model.CollectionModel as DomainCollectionModel
import com.collection.kubera.domain.model.Shop as DomainShop
import com.collection.kubera.domain.model.User as DomainUser
import java.util.UUID

/**
 * Converts domain models to Room entities. Genaral flavor only.
 */
fun DomainUser.toUserEntity(): UserEntity = UserEntity(
    id = id.ifEmpty { UUID.randomUUID().toString() },
    username = username,
    password = password,
    status = status,
    loggedintime = loggedInTimeMillis,
)

fun DomainShop.toShopEntity(): ShopEntity = ShopEntity(
    id = id.ifEmpty { UUID.randomUUID().toString() },
    shopName = shopName,
    sShopName = sShopName,
    location = location,
    landmark = landmark,
    firstName = firstName,
    sFirstName = sFirstName,
    lastName = lastName,
    sLastName = sLastName,
    phoneNumber = phoneNumber,
    secondPhoneNumber = secondPhoneNumber,
    mailId = mailId,
    balance = balance,
    timestamp = timestampMillis,
    status = status,
)

fun DomainCollectionModel.toCollectionHistoryEntity(): CollectionHistoryEntity = CollectionHistoryEntity(
    id = id?.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString(),
    shopId = shopId,
    shopName = shopName,
    sShopName = sShopName,
    firstName = firstName,
    sFirstName = sFirstName,
    lastName = lastName,
    sLastName = sLastName,
    phoneNumber = phoneNumber,
    secondPhoneNumber = secondPhoneNumber,
    mailId = mailId,
    amount = amount,
    collectedBy = collectedBy,
    collectedById = collectedById,
    transactionType = transactionType,
    timestamp = timestampMillis,
)
