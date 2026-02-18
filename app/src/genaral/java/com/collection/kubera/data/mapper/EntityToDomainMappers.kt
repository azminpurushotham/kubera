package com.collection.kubera.data.mapper

import com.collection.kubera.data.local.entity.CollectionHistoryEntity
import com.collection.kubera.data.local.entity.ShopEntity
import com.collection.kubera.data.local.entity.TodaysCollectionEntity
import com.collection.kubera.data.local.entity.UserEntity
import com.collection.kubera.domain.model.CollectionModel as DomainCollectionModel
import com.collection.kubera.domain.model.Shop as DomainShop
import com.collection.kubera.domain.model.TodaysCollectionData as DomainTodaysCollectionData
import com.collection.kubera.domain.model.User as DomainUser

/**
 * Converts Room entities to domain models. Genaral flavor only.
 */
fun UserEntity.toDomainUser(): DomainUser = DomainUser(
    id = id,
    username = username,
    password = password,
    status = status,
    loggedInTimeMillis = loggedintime,
)

fun ShopEntity.toDomainShop(): DomainShop = DomainShop(
    id = id,
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
    timestampMillis = timestamp,
    status = status,
)

fun CollectionHistoryEntity.toDomainCollectionModel(): DomainCollectionModel = DomainCollectionModel(
    id = id,
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
    timestampMillis = timestamp,
)

fun TodaysCollectionEntity.toDomainTodaysCollectionData(): DomainTodaysCollectionData =
    DomainTodaysCollectionData(balance = balance, credit = credit, debit = debit)
