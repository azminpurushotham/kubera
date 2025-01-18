package com.collection.kubera.ui

import androidx.navigation.NavHostController
import com.collection.kubera.ui.AllDestinations.PROFILE
import com.collection.kubera.ui.AllDestinations.SHOP_LIST
import com.collection.kubera.ui.AllDestinations.ADD_NEW_SHOP
import com.collection.kubera.ui.AllDestinations.COLLECTION_HISTORY
import com.collection.kubera.ui.AllDestinations.LOGOUT
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS

object AllDestinations {
    const val SHOP_LIST = "Shop List"
    const val COLLECTION_HISTORY = "Collection History"
    const val PROFILE = "Profile"
    const val ADD_NEW_SHOP = "Add New Shop"
    const val SHOP_DETAILS = "Shop Details"
    const val LOGOUT = "Logout"
}

class AppNavigationActions(private val navController: NavHostController) {
    fun navigateToShopList() {
        navController.navigate(SHOP_LIST) {
            popUpTo(SHOP_LIST){inclusive = false}
        }
    }
    fun navigateToCollectionHistory() {
        navController.navigate(COLLECTION_HISTORY) {
            popUpTo(COLLECTION_HISTORY){inclusive = false}
        }
    }
    fun navigateToProfile() {
        navController.navigate(PROFILE) {
            popUpTo(SHOP_LIST){inclusive = false}
        }
    }
    fun navigateToAddNewShop() {
        navController.navigate(ADD_NEW_SHOP) {
            launchSingleTop = true
            popUpTo(SHOP_LIST)
        }
    }
    fun navigateToLogOut() {
        navController.navigate(LOGOUT) {
            popUpTo(LOGOUT)
        }
    }
    fun navigateToShopDetails() {
        navController.navigate(SHOP_DETAILS) {
            launchSingleTop = true
            popUpTo(SHOP_LIST)
        }
    }
}
