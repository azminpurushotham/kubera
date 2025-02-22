package com.collection.kubera.ui.shoplist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.states.HomeUiState
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
    val balance by viewModel.balance.collectAsState()
    val todaysCredit by viewModel.todaysCredit.collectAsState()
    val todaysDebit by viewModel.todaysDebit.collectAsState()
    val todaysCollection by viewModel.todaysCollection.collectAsState()
    val shopName by remember { mutableStateOf("") }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val shops = viewModel.shopFlow.collectAsLazyPagingItems()

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
        viewModel.onRefresh()
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
                Header(shopName, viewModel, todaysCollection, todaysCredit, todaysDebit, balance)
            }

            items(shops.itemSnapshotList) { shop ->
                shop?.let { item ->
                    ShopItem(navController, item)
                }
            }

            // Show loading indicator
            if (shops.loadState.append is  LoadState.Loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}






