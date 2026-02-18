package com.collection.kubera.domain.model

/**
 * Domain CollectionModel entity. Pure Kotlin, no framework dependency.
 */
data class CollectionModel(
    val id: String? = null,
    val shopId: String? = null,
    val shopName: String? = null,
    val sShopName: String? = null,
    val firstName: String? = null,
    val sFirstName: String? = null,
    val lastName: String? = null,
    val sLastName: String? = null,
    val phoneNumber: String? = null,
    val secondPhoneNumber: String? = null,
    val mailId: String? = null,
    val amount: Long? = null,
    val collectedBy: String = "Admin",
    val collectedById: String? = null,
    val transactionType: String? = null,
    val timestampMillis: Long = 0L,
)
