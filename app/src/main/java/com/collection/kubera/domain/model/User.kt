package com.collection.kubera.domain.model

/**
 * Domain User entity. Pure Kotlin, no framework dependency.
 */
data class User(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val status: Boolean = false,
    val loggedInTimeMillis: Long = 0L,
)
