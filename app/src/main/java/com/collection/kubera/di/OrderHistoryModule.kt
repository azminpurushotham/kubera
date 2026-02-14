package com.collection.kubera.di

import com.collection.kubera.data.repository.TransactionHistoryRepository
import com.collection.kubera.data.repository.TransactionHistoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderHistoryModule {

    @Provides
    @Singleton
    fun provideTransactionHistoryRepository(): TransactionHistoryRepository =
        TransactionHistoryRepositoryImpl()
}
