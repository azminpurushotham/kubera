package com.collection.kubera.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.collection.kubera.ui.addnewshop.AddNewShopScreen
import com.collection.kubera.ui.orderhistory.CollectionHistory
import com.collection.kubera.ui.shopdetails.ShopDetailsScreen
import com.collection.kubera.ui.shoplist.ShopListScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {

    val navController: NavHostController = rememberNavController()
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: AllDestinations.SHOP_LIST
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }


    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer(
                route = currentRoute,
                navigateToShopList = { navigationActions.navigateToShopList() },
                navigateToCollectionHistory = { navigationActions.navigateToCollectionHistory() },
                navigateToProfile = { navigationActions.navigateToProfile() },
                navigateToAddNewShop = { navigationActions.navigateToAddNewShop() },
                navigateToLogout = { navigationActions.navigateToLogOut() },
                closeDrawer = { coroutineScope.launch { drawerState.close() } },
                modifier = Modifier
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = getTitle(currentRoute)) },
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = {
                            when (currentRoute) {
                                AllDestinations.SHOP_LIST -> {
                                    coroutineScope.launch { drawerState.open() }
                                }

                                else -> coroutineScope.launch {
                                    navController.popBackStack()
                                }
                            }
                        }, content = {
                            Icon(
                                imageVector = getIcon(currentRoute),
                                contentDescription = null
                            )
                        })
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BottomAppBarItem(
                            iconResId = Icons.Default.Home,
                            label = "Home",
                            onClick = {
                                navigationActions.navigateToShopList()
                            }
                        )
                        BottomAppBarItem(
                            iconResId = Icons.Default.History,
                            label = "Orders",
                            onClick = {
                                navigationActions.navigateToCollectionHistory()
                            }
                        )
                        BottomAppBarItem(
                            iconResId = Icons.Default.Add,
                            label = "Orders",
                            onClick = {
                                navigationActions.navigateToAddNewShop()
                            }
                        )
                        BottomAppBarItem(
                            iconResId = Icons.Default.Person,
                            label = "Profile",
                            onClick = {
                                navigationActions.navigateToProfile()
                            }
                        )
                        BottomAppBarItem(
                            iconResId = Icons.Default.Logout,
                            label = "Profile",
                            onClick = {
                            }
                        )
                    }
                }
            },
            modifier = Modifier
        ) {
            NavHost(
                navController = navController,
                startDestination = AllDestinations.SHOP_LIST,
                modifier = modifier.padding(it)
            ) {
                composable(AllDestinations.SHOP_LIST) {
                    ShopListScreen(navController)
                }
                composable(AllDestinations.COLLECTION_HISTORY) {
                    CollectionHistory(navController)
                }
                composable(AllDestinations.ADD_NEW_SHOP) {
                    AddNewShopScreen(navController)
                }
                composable("${AllDestinations.SHOP_DETAILS}/{shopId}") { backStackEntry ->
                    val shopId = backStackEntry.arguments?.getString("shopId")
                    ShopDetailsScreen(shopId, navController)
                }
            }
        }
    }


}

@Composable
fun BottomAppBarItem(
    iconResId: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            iconResId,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Normal
        )
    }
}

fun getIcon(currentRoute: String): ImageVector {
    when (currentRoute) {
        AllDestinations.SHOP_LIST -> return Icons.Default.Menu
    }
    return Icons.Default.ArrowBackIosNew
}

fun getTitle(currentRoute: String): String {
    if (currentRoute.contains(AllDestinations.SHOP_DETAILS)) {
        return "Details"
    }
    return when (currentRoute) {
        AllDestinations.SHOP_LIST -> AllDestinations.SHOP_LIST
        AllDestinations.COLLECTION_HISTORY -> AllDestinations.COLLECTION_HISTORY
        AllDestinations.PROFILE -> AllDestinations.PROFILE
        AllDestinations.ADD_NEW_SHOP -> AllDestinations.ADD_NEW_SHOP
        else -> AllDestinations.SHOP_LIST
    }

}
