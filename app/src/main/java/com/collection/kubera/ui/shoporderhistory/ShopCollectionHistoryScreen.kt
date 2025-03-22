package com.collection.kubera.ui.shoporderhistory

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.R
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.Shop
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.onprimaryD
import com.collection.kubera.ui.updatecredentials.UpdateCredentialsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopCollectionHistoryScreen(
    prm: Shop,
    navController: NavHostController,
    viewModel: ShopCollectionViewModel = viewModel(factory = ViewModelFactory(prm))
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shop by viewModel.shop.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val list = viewModel.list.collectAsLazyPagingItems()
    val userPagingItems: LazyPagingItems<DocumentSnapshot> = viewModel.list.collectAsLazyPagingItems()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getSwipeShopsCollectionHistory()
        viewModel.setShop(prm)
    }

    when (uiState) {
        is HomeUiState.Initial -> {
            viewModel.setShop(prm)
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

        LazyColumn(
            modifier = Modifier
//                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top, // Vertically center items
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
        ) {
            item {
                Header(shop, balance)
            }

            items(userPagingItems.itemCount) { index ->
                val item = userPagingItems[index]?.toObject(CollectionModel::class.java)
                item?.let {
                    ShopCollectionItem(item)
                }
            }

            // Show Loader at the Bottom During Load More
            item {
                if (userPagingItems.loadState.refresh is LoadState.Loading) {
                    Timber.i("loadState.refresh")
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                else if (userPagingItems.loadState.append is LoadState.Loading) {
                    Timber.i("loadState.append")
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                else if (userPagingItems.loadState.refresh is LoadState.Error) {
                    Timber.i("loadState.Error")
                    val e = list.loadState.refresh as LoadState.Error
                    Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

class ViewModelFactory(private val shop: Shop) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopCollectionViewModel::class.java)) {
            return ShopCollectionViewModel(shop) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



