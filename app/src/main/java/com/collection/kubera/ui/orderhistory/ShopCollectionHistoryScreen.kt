package com.collection.kubera.ui.orderhistory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.collection.kubera.R
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.AllDestinations.SHOP_COLLECTION_HISTORY
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS
import com.collection.kubera.ui.theme.backgroundD
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.onprimaryD
import com.collection.kubera.ui.theme.red
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopCollectionHistoryScreen(
    id: String? = null,
    navController: NavHostController,
    viewModel: ShopCollectionViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shopList by viewModel.shopList.collectAsState()
    val shop by viewModel.shop.collectAsState()
    val balance by viewModel.balance.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getSwipeShopsCollectionHistory(id)
        if (id != null) {
            viewModel.getShopDetails(id)
        }
    }

    when (uiState) {
        is HomeUiState.Initial -> {
            if (id != null) {
                viewModel.getCollectionHistory(id)
                viewModel.getShopDetails(id)
            }
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

        is HomeUiState.Refreshing -> {
            isRefreshing = true
        }
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            containerColor = boxColorD,
            content = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                ) {
                    Text(
                        "Sort By",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight(600),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            "Alphabetically",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        )

                        Spacer(modifier = Modifier.weight(1f)) // Spacer to fill the space

                        Row{
                            Button(onClick = {

                            }) {
                                Text(
                                    "A - Z",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                )
                            }
                            Spacer(modifier = Modifier.width(60.dp))
                            Button(
                                onClick = {

                                }) {
                                Text(
                                    "Z - A",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            "Entry Time",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        )
                        Spacer(modifier = Modifier.weight(1f)) // Spacer to fill the space
                        Button(
                            onClick = {

                            }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_arrow_upward_24), // Replace with your drawable
                                contentDescription = stringResource(R.string.filter),
                                alignment = Alignment.CenterEnd,
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.tint(onprimaryD)
                            )
                        }
                        Spacer(modifier = Modifier.width(60.dp))
                        Button(onClick = {

                        }) {
                            Image(
                                modifier = Modifier.rotate(180f),
                                painter = painterResource(id = R.drawable.baseline_arrow_upward_24), // Replace with your drawable
                                contentDescription = stringResource(R.string.filter),
                                alignment = Alignment.CenterEnd,
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.tint(onprimaryD)
                            )
                        }
                    }
                }
            },
            onDismissRequest = {
                showBottomSheet = false
            },
        )
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
            Button(
                colors = ButtonDefaults.buttonColors(backgroundD),
                onClick = {
                    showBottomSheet = true
                },
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_filter_list_24), // Replace with your drawable
                    contentDescription = stringResource(R.string.filter),
                    alignment = Alignment.CenterEnd,
                    contentScale = ContentScale.Crop, // Adjust image scaling
                        modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(onprimaryD) // Optional color filter
                )
            }
            Card(
                elevation = CardDefaults.cardElevation(20.dp),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(color = boxColorD)
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
                            "${shop?.firstName} ${shop?.lastName}",
                            fontWeight = FontWeight(600),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "${shop?.shopName}",
                            fontWeight = FontWeight(400),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
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
                ElevatedCard(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = boxColorD,
                    ),
                    onClick = {
                        Timber.v("SHOP_DETAILS")
                        navController.navigate("${SHOP_DETAILS}/${item.shopId}") {
                            popUpTo(SHOP_COLLECTION_HISTORY) {
                                inclusive = false
                            }
                        }
                    }) {
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 16.dp
                            ).align(Alignment.Start)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            item.datedmy,
                            fontWeight = FontWeight(100),
                            modifier = Modifier.width(100.dp),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            item.time,
                            fontWeight = FontWeight(100),
                            modifier = Modifier.width(100.dp),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            item.transactionType ?: "",
                            fontWeight = FontWeight(100),
                            modifier = Modifier.width(100.dp),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = if (item.transactionType == "Credit") green else red,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            (item.amount ?: 0.0).toString(),
                            fontWeight = FontWeight(500),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = if (item.transactionType == "Credit") green else red,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}





