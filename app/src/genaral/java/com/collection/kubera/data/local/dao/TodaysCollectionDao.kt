package com.collection.kubera.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.collection.kubera.data.local.entity.TodaysCollectionEntity

@Dao
interface TodaysCollectionDao {

    @Query("SELECT * FROM todays_collection ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(): TodaysCollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: TodaysCollectionEntity)

    @Update
    fun update(entity: TodaysCollectionEntity)

    @Query("UPDATE todays_collection SET balance = :balance, credit = :credit, debit = :debit WHERE id = :id")
    fun update(id: String, balance: Long, credit: Long, debit: Long)

    @Query("DELETE FROM todays_collection")
    fun deleteAll()
}
