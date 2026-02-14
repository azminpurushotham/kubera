package com.collection.kubera.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.collection.kubera.data.local.dao.BalanceDao
import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.local.dao.ShopDao
import com.collection.kubera.data.local.dao.TodaysCollectionDao
import com.collection.kubera.data.local.dao.UserDao
import com.collection.kubera.data.local.entity.BalanceEntity
import com.collection.kubera.data.local.entity.CollectionHistoryEntity
import com.collection.kubera.data.local.entity.ShopEntity
import com.collection.kubera.data.local.entity.TodaysCollectionEntity
import com.collection.kubera.data.local.entity.UserEntity

@Database(
    entities = [
        ShopEntity::class,
        CollectionHistoryEntity::class,
        TodaysCollectionEntity::class,
        BalanceEntity::class,
        UserEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class KuberaDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
    abstract fun collectionHistoryDao(): CollectionHistoryDao
    abstract fun todaysCollectionDao(): TodaysCollectionDao
    abstract fun balanceDao(): BalanceDao
    abstract fun userDao(): UserDao
}
