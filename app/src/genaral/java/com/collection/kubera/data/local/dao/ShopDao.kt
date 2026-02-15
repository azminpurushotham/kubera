package com.collection.kubera.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.collection.kubera.data.local.entity.ShopEntity

@Dao
interface ShopDao {

    @Query("SELECT * FROM shop ORDER BY s_firstName ASC")
    fun getShopsPagingSource(): PagingSource<Int, ShopEntity>

    @Query("SELECT * FROM shop WHERE s_firstName >= :query ORDER BY s_firstName ASC")
    fun getShopsSearchPagingSource(query: String): PagingSource<Int, ShopEntity>

    @Query("SELECT * FROM shop WHERE id = :id")
    fun getById(id: String): List<ShopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(shop: ShopEntity)

    @Update
    fun update(shop: ShopEntity)

    @Query("UPDATE shop SET balance = :balance WHERE id = :id")
    fun updateBalance(id: String, balance: Long)

    @Query("SELECT * FROM shop ORDER BY s_firstName ASC")
    fun getAllShops(): List<ShopEntity>
}
