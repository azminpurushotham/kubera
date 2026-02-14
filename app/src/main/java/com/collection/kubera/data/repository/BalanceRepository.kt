package com.collection.kubera.data.repository

import com.collection.kubera.data.Result

interface BalanceRepository {
    suspend fun getBalance(): Result<Long>
    suspend fun updateBalance(delta: Long, isCredit: Boolean): Result<Unit>
}
