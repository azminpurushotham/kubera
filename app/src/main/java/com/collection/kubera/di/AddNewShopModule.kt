package com.collection.kubera.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AddNewShopModule {
    // ShopRepository, CollectionHistoryRepository, BalanceRepository,
    // TodaysCollectionRepository, UserPreferencesRepository provided by ShopListModule & ShopDetailsModule
}
