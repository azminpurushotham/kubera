package com.collection.kubera.domain.model

/**
 * Domain result of Google auth. Pure Kotlin.
 */
data class GoogleAuthResult(
    val userId: String,
    val displayName: String,
    val email: String?,
)
