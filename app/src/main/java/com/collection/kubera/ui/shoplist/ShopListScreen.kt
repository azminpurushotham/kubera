package com.collection.kubera.ui.shoplist

import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS
import com.collection.kubera.ui.AllDestinations.SHOP_LIST
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.onHintD
import com.collection.kubera.ui.theme.red
import com.google.gson.Gson
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopListScreen(
    navController: NavHostController,
    viewModel: ShopListViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState = remember { mutableStateOf(Lifecycle.Event.ON_CREATE) } // Initialize
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shopList by viewModel.shopList.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val todaysCredit by viewModel.todaysCredit.collectAsState()
    val todaysDebit by viewModel.todaysDebit.collectAsState()
    val todaysCollection by viewModel.todaysCollection.collectAsState()
    var shopName by remember { mutableStateOf("") }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState.value = event // Update lifecycle state
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isResumed = lifecycleState.value == Lifecycle.Event.ON_RESUME
    // Now you can use 'isResumed' to perform actions conditionally:
    if (isResumed) {
        // Code to execute only when the Composable is resumed
        LaunchedEffect(Unit) { // Use LaunchedEffect for side effects
            Timber.i("Composable is resumed!")
//            viewModel.getSwipeShopsOnResume()
        }
    } else if (lifecycleState.value == Lifecycle.Event.ON_PAUSE) {
        // Code to execute when paused
        Timber.i("Composable is paused!")
    } else if (lifecycleState.value == Lifecycle.Event.ON_DESTROY) {
        Timber.i("Composable is destroyed!")
    }


    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getSwipeShops()
        viewModel.getBalance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewModel.getTodaysCollectionLogic()
        }
    }

    when (uiState) {
        is HomeUiState.Initial -> {
            viewModel.getShops()
            viewModel.getBalance()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                viewModel.getTodaysCollectionLogic()
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

    PullToRefreshBox(
        state = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyColumn {

        }
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
                        unfocusedLabelColor = onHintD,
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
                            "Today's Collection",
                            fontWeight = FontWeight(600),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Row {
                            Text(
                                todaysCollection.toString(),
                                fontWeight = FontWeight(400),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color = if (todaysCollection>0) green else red,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                todaysCredit.toString(),
                                fontWeight = FontWeight(400),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color = green,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                todaysDebit.toString(),
                                fontWeight = FontWeight(400),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color =   red,
                            )
                        }
                    }
                    Text(
                        balance.toString(),
                        fontWeight = FontWeight(400),
                        fontSize = 30.sp,
                        color = if (balance>0) green else red,
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
                        navController.navigate("${SHOP_DETAILS}?${Gson().toJson(item)}"){
                            popUpTo(SHOP_LIST){
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
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "${item.firstName} ${item.lastName}",
                                fontWeight = FontWeight(500),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                item.shopName,
                                fontWeight = FontWeight(1),
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                (item.balance ?: 0.0).toString(),
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
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        @Composable
        fun PaginatedList() {
            /*val lazyPagingItems = viewModel.items.collectAsLazyPagingItems()

                LazyColumn {
                items(lazyPagingItems) { item ->
                    Text(text = item ?: "Loading...")
                }

                lazyPagingItems.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator() }
                        }
                        loadState.append is LoadState.Error -> {
                            item {
                                Text("Error loading more items")
                            }
                        }
                    }
                }
            }*/
        }
    }
}





