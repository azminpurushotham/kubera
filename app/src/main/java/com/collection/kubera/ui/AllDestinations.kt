package com.collection.kubera.ui

import androidx.navigation.NavHostController
import com.collection.kubera.ui.AllDestinations.PROFILE
import com.collection.kubera.ui.AllDestinations.SHOP_LIST
import com.collection.kubera.ui.AllDestinations.ADD_NEW_SHOP
import com.collection.kubera.ui.AllDestinations.LOGOUT

object AllDestinations {
    const val SHOP_LIST = "shoplist"
    const val PROFILE = "profile"
    const val ADD_NEW_SHOP = "add_new_shop"
    const val LOGOUT = "logout"
}

class AppNavigationActions(private val navController: NavHostController) {
    fun navigateToShopList() {
        navController.navigate(SHOP_LIST) {
            popUpTo(SHOP_LIST)
        }
    }
    fun navigateToProfile() {
        navController.navigate(PROFILE) {
            popUpTo(PROFILE)
        }
    }
    fun navigateToAddNewShop() {
        navController.navigate(ADD_NEW_SHOP) {
            popUpTo(ADD_NEW_SHOP)
        }
    }
    fun navigateToLogOut() {
        navController.navigate(LOGOUT) {
            popUpTo(LOGOUT)
        }
    }
}
