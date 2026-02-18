package com.collection.kubera.domain.model

/**
 * Domain data representing today's collection totals. Pure Kotlin.
 */
data class TodaysCollectionData(
    val balance: Long,
    val credit: Long,
    val debit: Long,
)
