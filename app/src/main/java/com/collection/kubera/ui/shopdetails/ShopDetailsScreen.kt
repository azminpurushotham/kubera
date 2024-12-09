package com.collection.kubera.ui.shopdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.states.ShopDetailUiState
import com.collection.kubera.ui.theme.headingLabelD

@Preview
@Composable
fun ShopDetailsScreen(
    id: String? = null,
    viewModel: ShopDetailsViewModel = viewModel(), 
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shop by viewModel.shop.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
    ) {

        when (uiState) {
            is ShopDetailUiState.Initial -> {
//                id?.let { viewModel.getShopDetails(it) }
            }

            ShopDetailUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            is ShopDetailUiState.ShopDetailInit -> {

            }

            is ShopDetailUiState.ShopDetailSuccess -> {
            }

            is ShopDetailUiState.ShopDetailError -> {

            }

        }

        Text(
            "Enter Shop Details",
            fontSize = 18.sp,
            fontWeight = FontWeight(1),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = shop?.shopName?:"",
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = headingLabelD,
        )
        Text(
            text = shop?.location?:"",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = shop?.landmark?:"",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = (shop?.balance?:0).toString(),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(60.dp))
        Text(
            "Enter Owner Details",
            fontSize = 18.sp,
            fontWeight = FontWeight(1),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = shop?.firstName?:"",
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = shop?.lastName?:"",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = shop?.phoneNumber?:"",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = shop?.secondPhoneNumber?:"",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = shop?.mailId?:"",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}


