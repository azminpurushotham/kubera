package com.collection.kubera.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.ui.shoplist.ShopListScreen
import com.collection.kubera.ui.theme.KuberaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    viewModel.getUsers()

    KuberaTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .defaultMinSize(minWidth = 280.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(start = 0.dp, end = 0.dp, bottom = 16.dp, top = 20.dp),
                ) {
                    Button(
                        onClick = {

                        },
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 280.dp)
                            .height(56.dp)
                            .align(Alignment.Start),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 280.dp),
                            contentAlignment = Alignment.CenterStart // Align content to the start
                        ) {
                            Text(
                                text = "Shop List",
                                fontSize = 16.sp,
                                fontWeight = FontWeight(weight = 400)
                            )
                        }
                    }
                    Button(
                        onClick = {
                        },
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 280.dp)
                            .height(56.dp)
                            .align(Alignment.Start),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 280.dp),
                            contentAlignment = Alignment.CenterStart // Align content to the start
                        ) {
                            Text(
                                text = "Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight(weight = 400)
                            )
                        }
                    }
                    Button(
                        onClick = {
                        },
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 280.dp)
                            .height(56.dp)
                            .align(Alignment.Start),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 280.dp),
                            contentAlignment = Alignment.CenterStart // Align content to the start
                        ) {
                            Text(
                                text = "Add New Shop",
                                fontSize = 16.sp,
                                fontWeight = FontWeight(weight = 400)
                            )
                        }
                    }
                    Button(
                        onClick = {

                        },
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 280.dp)
                            .height(56.dp)
                            .align(Alignment.Start),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 280.dp),
                            contentAlignment = Alignment.CenterStart // Align content to the start
                        ) {
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight(weight = 400)
                            )
                        }
                    }
                }
            },
            content = {
                Scaffold(topBar = {
                    TopAppBar(
                        title = { Text("Shop List", fontSize = 18.sp) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                },
                    content = { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                            ShopListScreen()
                        }
                    }
                )
            }
        )

    }
}



