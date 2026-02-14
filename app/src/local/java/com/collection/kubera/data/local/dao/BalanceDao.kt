package com.collection.kubera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.collection.kubera.data.local.entity.BalanceEntity

@Dao
interface BalanceDao {

    @Query("SELECT * FROM balance ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): BalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BalanceEntity)

    @Query("UPDATE balance SET balance = :balance WHERE id = :id")
    suspend fun updateBalance(id: String, balance: Long)
}
