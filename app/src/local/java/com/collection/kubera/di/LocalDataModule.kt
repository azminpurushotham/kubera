package com.collection.kubera.di

import com.collection.kubera.data.repository.BalanceRepository
import com.collection.kubera.data.repository.BalanceRepositoryImpl
import com.collection.kubera.data.repository.CollectionHistoryRepository
import com.collection.kubera.data.repository.CollectionHistoryRepositoryImpl
import com.collection.kubera.data.repository.GoogleAuthRepository
import com.collection.kubera.data.repository.GoogleAuthRepositoryImpl
import com.collection.kubera.data.repository.ReportRepository
import com.collection.kubera.data.repository.ReportRepositoryImpl
import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.ShopRepositoryImpl
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.data.repository.TodaysCollectionRepositoryImpl
import com.collection.kubera.data.repository.TransactionHistoryRepository
import com.collection.kubera.data.repository.TransactionHistoryRepositoryImpl
import com.collection.kubera.data.repository.UserRepository
import com.collection.kubera.data.repository.UserRepositoryImpl
import com.collection.kubera.data.local.dao.BalanceDao
import com.collection.kubera.data.local.dao.CollectionHistoryDao
import com.collection.kubera.data.local.dao.ShopDao
import com.collection.kubera.data.local.dao.TodaysCollectionDao
import com.collection.kubera.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Provides
    @Singleton
    fun provideShopRepository(shopDao: ShopDao): ShopRepository = ShopRepositoryImpl(shopDao)

    @Provides
    @Singleton
    fun provideTodaysCollectionRepository(dao: TodaysCollectionDao): TodaysCollectionRepository =
        TodaysCollectionRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideTransactionHistoryRepository(dao: CollectionHistoryDao): TransactionHistoryRepository =
        TransactionHistoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepositoryImpl(userDao)

    @Provides
    @Singleton
    fun provideCollectionHistoryRepository(dao: CollectionHistoryDao): CollectionHistoryRepository =
        CollectionHistoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideBalanceRepository(balanceDao: BalanceDao): BalanceRepository =
        BalanceRepositoryImpl(balanceDao)

    @Provides
    @Singleton
    fun provideReportRepository(
        collectionHistoryDao: CollectionHistoryDao,
        shopDao: ShopDao
    ): ReportRepository = ReportRepositoryImpl(collectionHistoryDao, shopDao)

    @Provides
    @Singleton
    fun provideGoogleAuthRepository(): GoogleAuthRepository = GoogleAuthRepositoryImpl()
}
