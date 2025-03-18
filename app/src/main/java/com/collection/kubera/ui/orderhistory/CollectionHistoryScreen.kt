package com.collection.kubera.ui.orderhistory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.R
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.theme.backgroundD
import com.collection.kubera.ui.theme.onprimaryD
import com.google.firebase.firestore.DocumentSnapshot
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionHistoryScreen(
    navController: NavHostController,
    viewModel: CollectionViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val list = viewModel.list.collectAsLazyPagingItems()
    val userPagingItems: LazyPagingItems<DocumentSnapshot> = viewModel.list.collectAsLazyPagingItems()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getSwipeShopsCollectionHistory()
//        viewModel.getBalance()
        viewModel.getTodaysCollection()
    }

    when (uiState) {
        is HomeUiState.Initial -> {
            viewModel.init()
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
        ShowCollectionSort(
            viewModel,
            sheetState
        ) {
            showBottomSheet = false
        }
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
                Column {
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
                    BalanceHeader(viewModel)
                }
            }

            items(userPagingItems.itemCount) { index ->
                val item = userPagingItems[index]?.toObject(CollectionModel::class.java)
                item?.let {
                    CollectionItem(navController, item)
                }
            }

            // Handle loading states (optional)
            list.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                    loadState.append is LoadState.NotLoading ->{
                        Timber.i("NotLoading")
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = list.loadState.refresh as LoadState.Error
                        item { Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.onPrimary) }
                    }
                }
            }

        }
    }
}






