package com.collection.kubera.ui

import androidx.navigation.NavHostController
import com.collection.kubera.ui.AllDestinations.PROFILE
import com.collection.kubera.ui.AllDestinations.SHOP_LIST
import com.collection.kubera.ui.AllDestinations.ADD_NEW_SHOP
import com.collection.kubera.ui.AllDestinations.LOGOUT
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS

object AllDestinations {
    const val SHOP_LIST = "Shop List"
    const val PROFILE = "Profile"
    const val ADD_NEW_SHOP = "Add New Shop"
    const val SHOP_DETAILS = "Shop Details"
    const val LOGOUT = "Logout"
}

class AppNavigationActions(private val navController: NavHostController) {
    fun navigateToShopList() {
        navController.navigate(SHOP_LIST) {
            popUpTo(SHOP_LIST)
        }
    }
    fun navigateToProfile() {
        navController.navigate(PROFILE) {
            popUpTo(SHOP_LIST)
        }
    }
    fun navigateToAddNewShop() {
        navController.navigate(ADD_NEW_SHOP) {
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
            popUpTo(SHOP_LIST)
        }
    }
}
