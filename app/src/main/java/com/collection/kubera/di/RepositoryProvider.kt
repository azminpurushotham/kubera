package com.collection.kubera.di

import com.collection.kubera.data.repository.ShopRepository
import com.collection.kubera.data.repository.ShopRepositoryImpl
import com.collection.kubera.data.repository.TodaysCollectionRepository
import com.collection.kubera.data.repository.TodaysCollectionRepositoryImpl

/**
 * Lightweight service locator for repositories.
 * Use Hilt/Koin for full DI when the project grows.
 */
object RepositoryProvider {

    val shopRepository: ShopRepository by lazy { ShopRepositoryImpl() }
    val todaysCollectionRepository: TodaysCollectionRepository by lazy {
        TodaysCollectionRepositoryImpl()
    }
}
