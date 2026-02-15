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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudDataModule {

    @Provides
    @Singleton
    fun provideShopRepository(): ShopRepository = ShopRepositoryImpl()

    @Provides
    @Singleton
    fun provideTodaysCollectionRepository(): TodaysCollectionRepository =
        TodaysCollectionRepositoryImpl()

    @Provides
    @Singleton
    fun provideTransactionHistoryRepository(): TransactionHistoryRepository =
        TransactionHistoryRepositoryImpl()

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @Provides
    @Singleton
    fun provideCollectionHistoryRepository(): CollectionHistoryRepository =
        CollectionHistoryRepositoryImpl()

    @Provides
    @Singleton
    fun provideBalanceRepository(): BalanceRepository = BalanceRepositoryImpl()

    @Provides
    @Singleton
    fun provideReportRepository(): ReportRepository = ReportRepositoryImpl()

    @Provides
    @Singleton
    fun provideGoogleAuthRepository(): GoogleAuthRepository = GoogleAuthRepositoryImpl()
}
