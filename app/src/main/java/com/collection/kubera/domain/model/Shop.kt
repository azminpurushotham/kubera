package com.collection.kubera.domain.model

/**
 * Domain Shop entity. Pure Kotlin, no framework dependency.
 */
data class Shop(
    val id: String = "",
    val shopName: String = "",
    val sShopName: String = "",
    val location: String = "",
    val landmark: String? = "",
    val firstName: String = "",
    val sFirstName: String = "",
    val lastName: String = "",
    val sLastName: String = "",
    val phoneNumber: String? = null,
    val secondPhoneNumber: String? = null,
    val mailId: String = "",
    val balance: Long? = null,
    val timestampMillis: Long = 0L,
    val status: Boolean = true,
)
