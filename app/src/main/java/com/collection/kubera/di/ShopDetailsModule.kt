package com.collection.kubera.di

import com.collection.kubera.data.repository.UserPreferencesRepository
import com.collection.kubera.data.repository.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
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
    // CollectionHistoryRepository, BalanceRepository provided by CloudDataModule / LocalDataModule per flavor
}
