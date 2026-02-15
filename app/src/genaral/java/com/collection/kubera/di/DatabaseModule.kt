package com.collection.kubera.di

import android.content.Context
import androidx.room.Room
import com.collection.kubera.data.local.KuberaDatabase
import com.collection.kubera.data.local.dao.BalanceDao
import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.local.dao.ShopDao
import com.collection.kubera.data.local.dao.TodaysCollectionDao
import com.collection.kubera.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KuberaDatabase =
        Room.databaseBuilder(
            context,
            KuberaDatabase::class.java,
            "kubera.db"
        ).build()

    @Provides
    @Singleton
    fun provideShopDao(db: KuberaDatabase): ShopDao = db.shopDao()

    @Provides
    @Singleton
    fun provideCollectionHistoryDao(db: KuberaDatabase): CollectionHistoryDao = db.collectionHistoryDao()

    @Provides
    @Singleton
    fun provideTodaysCollectionDao(db: KuberaDatabase): TodaysCollectionDao = db.todaysCollectionDao()

    @Provides
    @Singleton
    fun provideBalanceDao(db: KuberaDatabase): BalanceDao = db.balanceDao()

    @Provides
    @Singleton
    fun provideUserDao(db: KuberaDatabase): UserDao = db.userDao()
}
