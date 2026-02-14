package com.collection.kubera.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UpdateCredentialsModule {
    // UserRepository provided by ProfileModule
}
