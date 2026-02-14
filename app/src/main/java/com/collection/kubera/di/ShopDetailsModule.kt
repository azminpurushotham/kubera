package com.collection.kubera.di

import com.collection.kubera.data.repository.BalanceRepository
import com.collection.kubera.data.repository.BalanceRepositoryImpl
import com.collection.kubera.data.repository.CollectionHistoryRepository
import com.collection.kubera.data.repository.CollectionHistoryRepositoryImpl
import com.collection.kubera.data.repository.UserPreferencesRepository
import com.collection.kubera.data.repository.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShopDetailsModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideCollectionHistoryRepository(): CollectionHistoryRepository =
            CollectionHistoryRepositoryImpl()

        @Provides
        @Singleton
        fun provideBalanceRepository(): BalanceRepository = BalanceRepositoryImpl()
    }
}
