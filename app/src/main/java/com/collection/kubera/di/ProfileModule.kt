package com.collection.kubera.di

import com.collection.kubera.data.repository.UserRepository
import com.collection.kubera.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()
}
