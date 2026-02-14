package com.collection.kubera.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    // UserRepository, UserPreferencesRepository provided by ProfileModule & ShopDetailsModule
}
