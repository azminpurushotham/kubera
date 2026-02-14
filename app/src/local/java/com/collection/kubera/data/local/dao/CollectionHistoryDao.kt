package com.collection.kubera.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.collection.kubera.data.local.entity.CollectionHistoryEntity

@Dao
interface CollectionHistoryDao {

    @Query("SELECT * FROM collection_history ORDER BY timestamp DESC")
    fun getTransactionHistoryPagingSource(): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history ORDER BY timestamp ASC")
    fun getTransactionHistoryAscPagingSource(): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history ORDER BY s_shopName ASC")
    fun getByShopNameAscPagingSource(): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history ORDER BY s_shopName DESC")
    fun getByShopNameDescPagingSource(): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history ORDER BY s_firstName ASC")
    fun getByFirstNameAscPagingSource(): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history ORDER BY s_firstName DESC")
    fun getByFirstNameDescPagingSource(): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history WHERE shopId = :shopId ORDER BY timestamp DESC")
    fun getShopCollectionPagingSource(shopId: String): PagingSource<Int, CollectionHistoryEntity>

    @Query("SELECT * FROM collection_history WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    suspend fun getByDateRange(start: Long, end: Long): List<CollectionHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CollectionHistoryEntity)
}
