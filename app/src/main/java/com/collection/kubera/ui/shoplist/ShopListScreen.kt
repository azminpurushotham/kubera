package com.collection.kubera.ui.shoplist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.data.Shop
import com.collection.kubera.states.HomeUiState
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopListScreen(
    navController: NavHostController,
    viewModel: ShopListViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycleState by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val listFlow by viewModel.list.collectAsState()
    val userPagingItems: LazyPagingItems<Shop> = listFlow.collectAsLazyPagingItems()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.Event.ON_RESUME) {
            Timber.d("Screen resumed")
            viewModel.onResume()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ShopListUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val onRefresh = {
        isRefreshing = true
        viewModel.onRefresh()
    }

    LaunchedEffect(userPagingItems.loadState.refresh) {
        if (userPagingItems.loadState.refresh !is LoadState.Loading) {
            isRefreshing = false
        }
    }

    when (uiState) {
        HomeUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        HomeUiState.Refreshing -> isRefreshing = true
        is HomeUiState.HomeSuccess,
        is HomeUiState.HomeInit,
        is HomeUiState.HomeError,
        HomeUiState.Initial,
        HomeUiState.Searching -> { /* isRefreshing cleared by loadState */ }
    }

    PullToRefreshBox(
        state = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Header(viewModel)
            }

            items(userPagingItems.itemCount) { index ->
                userPagingItems[index]?.let { shop -> ShopItem(navController, shop) }
            }

            item {
                when (val refreshLoadState = userPagingItems.loadState.refresh) {
                    is LoadState.Loading -> CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(10.dp)
                    )
                    is LoadState.Error -> Text(
                        text = "Error: ${refreshLoadState.error.localizedMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> when {
                        userPagingItems.loadState.append is LoadState.Loading ->
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(10.dp)
                            )
                        else -> { /* Idle */ }
                    }
                }
            }
        }
    }
}
