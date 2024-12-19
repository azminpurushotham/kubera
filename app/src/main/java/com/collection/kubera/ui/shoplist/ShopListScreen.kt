package com.collection.kubera.ui.shoplist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Indicator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ShopListScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: ShopListViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shopList by viewModel.shopList.collectAsState()
    val balance by viewModel.balance.collectAsState()
    var shopName by remember { mutableStateOf("") }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getSwipeShops()
        viewModel.getBalance()
    }

    when (uiState) {
        is HomeUiState.Initial -> {
            viewModel.getShops()
            viewModel.getBalance()
        }

        HomeUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        is HomeUiState.HomeInit -> {

        }

        is HomeUiState.Searching -> {

        }

        is HomeUiState.HomeSuccess -> {
            isRefreshing = false
            Timber.v("HomeSuccess")
        }

        is HomeUiState.HomeError -> {
            Toast.makeText(
                context,
                (uiState as HomeUiState.HomeError).errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }

        is HomeUiState.Refreshing->{
            isRefreshing = true
        }
    }

    PullToRefreshBox(
        state = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Vertically center items
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Box {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = {
                        shopName = it
                        viewModel.getShops(shopName)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    label = { Text("Search shops here...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 0.dp),
                    trailingIcon = {
                        val icon = Icons.Filled.Search
                        IconButton(onClick = {
                            viewModel.getShops(shopName)
                        }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                elevation = CardDefaults.cardElevation(20.dp),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "9:00 am",
                            fontWeight = FontWeight(600), fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "1-Dec-2024",
                            fontWeight = FontWeight(400), fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        balance.toString(),
                        fontWeight = FontWeight(400),
                        fontSize = 30.sp,
                        color = green,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            for (item in shopList) {
                Card(
                    onClick = {
                        Timber.v("SHOP_DETAILS")
                        navController.navigate("${SHOP_DETAILS}/${item.id}")
                    }) {
                    Row(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.background)
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "${item.firstName} ${item.lastName}",
                                fontWeight = FontWeight(400),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                item.shopName,
                                fontWeight = FontWeight(400),
                                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                (item.balance?:0.0).toString(),
                                fontWeight = FontWeight(500),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color = if ((item.balance ?: 0) > 0) green else red,
                                modifier = Modifier.align(Alignment.End)
                            )
                            Row(modifier = Modifier.align(Alignment.End)) {
                                Text(
                                    item.datedmy,
                                    fontWeight = FontWeight(400),
                                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    item.time,
                                    fontWeight = FontWeight(900),
                                    fontSize =  MaterialTheme.typography.labelSmall.fontSize,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}





