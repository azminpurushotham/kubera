package com.collection.kubera.ui.orderhistory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.collection.kubera.R
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.states.CollectionHistoryUiState
import com.collection.kubera.ui.theme.backgroundD
import com.collection.kubera.ui.theme.onprimaryD
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionHistoryScreen(
    navController: NavHostController,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val listFlow by viewModel.list.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val userPagingItems: LazyPagingItems<CollectionModel> = listFlow.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CollectionHistoryUiEvent.ShowError -> {
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
        is CollectionHistoryUiState.Initial -> { /* triggers init via LaunchedEffect */ }
        CollectionHistoryUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
        is CollectionHistoryUiState.CollectionHistoryUiStateInit,
        is CollectionHistoryUiState.CollectionHistoryUiStateSuccess -> { /* isRefreshing cleared by loadState */ }
        is CollectionHistoryUiState.CollectionHistoryUiStateError -> { /* errors via uiEvent */ }
        is CollectionHistoryUiState.Refreshing -> isRefreshing = true
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundD),
                        onClick = {
                            showBottomSheet = true
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_filter_list_24),
                            contentDescription = stringResource(R.string.filter),
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(30.dp),
                            colorFilter = ColorFilter.tint(onprimaryD)
                        )
                    }
                    BalanceHeader(viewModel)
                }
            }

            items(userPagingItems.itemCount) { index ->
                userPagingItems[index]?.let { item ->
                    CollectionItem(navController, item)
                }
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
