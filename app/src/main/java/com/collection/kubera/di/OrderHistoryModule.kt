package com.collection.kubera.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object OrderHistoryModule {
    // TransactionHistoryRepository provided by CloudDataModule / LocalDataModule per flavor
}
