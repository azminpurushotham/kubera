package com.collection.kubera.ui.orderhistory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.collection.kubera.R
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.theme.backgroundD
import com.collection.kubera.ui.theme.onprimaryD
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionHistoryScreen(
    navController: NavHostController,
    viewModel: CollectionViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shopList by viewModel.shopList.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getSwipeShopsCollectionHistory()
        viewModel.getBalance()
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
            sheetState,
            {
                showBottomSheet = false
            }
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
            BalanceHeader(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
            for (item in shopList) {
                if(item.shopId==null){
                    Timber.tag("NAME").i(item.shopName)
                    Timber.tag("ID").i(item.shopId)
                }
                CollectionItem(navController, item)
            }
        }
    }
}






