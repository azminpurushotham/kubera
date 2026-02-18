package com.collection.kubera.domain.repository

import com.collection.kubera.domain.model.Result

interface BalanceRepository {
    suspend fun getBalance(): Result<Long>
    suspend fun updateBalance(delta: Long, isCredit: Boolean): Result<Unit>
}
