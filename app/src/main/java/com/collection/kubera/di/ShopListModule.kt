package com.collection.kubera.di

import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.ShopRepositoryImpl
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.data.repository.TodaysCollectionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShopListModule {

    @Provides
    @Singleton
    fun provideShopRepository(): ShopRepository = ShopRepositoryImpl()

    @Provides
    @Singleton
    fun provideTodaysCollectionRepository(): TodaysCollectionRepository =
        TodaysCollectionRepositoryImpl()

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
