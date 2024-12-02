package com.collection.kubera.ui;

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AppDrawer(
        route: String,
        modifier: Modifier = Modifier,
        navigateToHome: () -> Unit = {},
        navigateToSettings: () -> Unit = {},
        closeDrawer: () -> Unit = {}
        ) {
ModalDrawerSheet(modifier = Modifier) {
    Spacer(modifier = Modifier.padding(5.dp))
    NavigationDrawerItem(
            label = {
                    Text(
                            text = "Shop List",
                            fontSize = 16.sp
                    )
            },
            selected = route == AllDestinations.SHOP_LIST,
            onClick = {
                    navigateToHome()
                    closeDrawer()
            },
            shape = MaterialTheme.shapes.small
    )
    NavigationDrawerItem(
            label = {
                    Text(
                            text = "Add New Shop",
                            fontSize = 16.sp
                    )
            },
            selected = route == AllDestinations.ADD_NEW_SHOP,
            onClick = {
                    navigateToHome()
                    closeDrawer()
            },
            shape = MaterialTheme.shapes.small
    )
    NavigationDrawerItem(
            label = {
                    Text(
                            text = "Profile",
                            fontSize = 16.sp
                    )
            },
            selected = route == AllDestinations.PROFILE,
            onClick = {
                    navigateToHome()
                    closeDrawer()
            },
            shape = MaterialTheme.shapes.small
    )
    NavigationDrawerItem(
            label = {
                    Text(
                            text = "Logout",
                            fontSize = 16.sp
                    )
            },
            selected = route == AllDestinations.LOGOUT,
            onClick = {
                    navigateToHome()
                    closeDrawer()
            },
            shape = MaterialTheme.shapes.small
    )

}
}
