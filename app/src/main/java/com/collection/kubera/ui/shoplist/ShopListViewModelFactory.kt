package com.collection.kubera.ui.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.collection.kubera.di.RepositoryProvider

class ShopListViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopListViewModel::class.java)) {
            return ShopListViewModel(
                shopRepository = RepositoryProvider.shopRepository,
                todaysCollectionRepository = RepositoryProvider.todaysCollectionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
