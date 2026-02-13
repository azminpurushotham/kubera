package com.collection.kubera.data

/**
 * Domain data representing today's collection totals.
 */
data class TodaysCollectionData(
    val balance: Long,
    val credit: Long,
    val debit: Long
)
