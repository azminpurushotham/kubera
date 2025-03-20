package com.collection.kubera.ui.shoplist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.data.Shop
import com.collection.kubera.states.HomeUiState
import com.google.firebase.firestore.DocumentSnapshot
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

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val list = viewModel.list.collectAsLazyPagingItems()
    val userPagingItems: LazyPagingItems<DocumentSnapshot> = viewModel.list.collectAsLazyPagingItems()

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
            viewModel.onResume()
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
                Header(viewModel)
            }

            items(userPagingItems.itemCount) { index ->
                val item = userPagingItems[index]?.toObject(Shop::class.java)
                item?.let {
                    ShopItem(navController, item)
                }
            }

            // Handle loading states (optional)
            list.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        Timber.i("loadState.refresh")
                        item {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        Timber.i("loadState.append")
                        item {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                    loadState.append is LoadState.NotLoading ->{
                        Timber.i("loadState.NotLoading")
                        Timber.i("NotLoading")
                    }
                    loadState.refresh is LoadState.Error -> {
                        Timber.i("loadState.refresh")
                        val e = list.loadState.refresh as LoadState.Error
                        item { Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.onPrimary) }
                    }
                }
            }
        }
    }
}






