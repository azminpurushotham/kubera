package com.collection.kubera.ui;

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AppDrawer(
    route: String,
    modifier: Modifier = Modifier,
    navigateToShopList: () -> Unit = {},
    navigateToProfile: () -> Unit = {},
    navigateToAddNewShop: () -> Unit = {},
    navigateToLogout: () -> Unit = {},
    closeDrawer: () -> Unit = {}
) {
    ModalDrawerSheet(
        modifier = Modifier.clip(
            RoundedCornerShape(0.dp)
        ),
        drawerContainerColor = MaterialTheme.colorScheme.primary) {
        Spacer(modifier = Modifier.padding(top = 30.dp))
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Shop List",
                    fontSize = 16.sp
                )
            },
            modifier = Modifier.clip(shape = RoundedCornerShape(0.dp)),
            selected = route == AllDestinations.SHOP_LIST,
            onClick = {
                navigateToShopList()
                closeDrawer()
            },
            shape = MaterialTheme.shapes.small,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.surface, // Background color when selected
                selectedTextColor = MaterialTheme.colorScheme.onSurface,    // Text color when selected
                unselectedContainerColor = MaterialTheme.colorScheme.primary, // Background when not selected
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary    // Text color when not selected
            )
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Add New Shop",
                    fontSize = 16.sp
                )
            },
            selected = route == AllDestinations.ADD_NEW_SHOP,
            modifier = Modifier.clip(shape = RoundedCornerShape(0.dp)),
            onClick = {
                navigateToAddNewShop()
                closeDrawer()
            },
            shape = MaterialTheme.shapes.small,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.surface, // Background color when selected
                selectedTextColor = MaterialTheme.colorScheme.onSurface,    // Text color when selected
                unselectedContainerColor = MaterialTheme.colorScheme.primary, // Background when not selected
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary    // Text color when not selected
            )
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Profile",
                    fontSize = 16.sp
                )
            },
            selected = route == AllDestinations.PROFILE,
            modifier = Modifier.clip(shape = RoundedCornerShape(0.dp)),
            onClick = {
                navigateToProfile()
                closeDrawer()
            },
            shape = MaterialTheme.shapes.small,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.surface, // Background color when selected
                selectedTextColor = MaterialTheme.colorScheme.onSurface,    // Text color when selected
                unselectedContainerColor = MaterialTheme.colorScheme.primary, // Background when not selected
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary    // Text color when not selected
            )
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Logout",
                    fontSize = 16.sp
                )
            },
            selected = route == AllDestinations.LOGOUT,
            modifier = Modifier.clip(shape = RoundedCornerShape(0.dp)),
            onClick = {
                navigateToLogout()
                closeDrawer()
            },
            shape = MaterialTheme.shapes.small,
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.surface, // Background color when selected
                selectedTextColor = MaterialTheme.colorScheme.onSurface,    // Text color when selected
                unselectedContainerColor = MaterialTheme.colorScheme.primary, // Background when not selected
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary    // Text color when not selected
            )
        )

    }
}
