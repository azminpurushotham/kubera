package com.collection.kubera.ui.orderhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue

@Composable
internal fun CollectionItem(
    navController: NavHostController,
    item: CollectionHistory
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var message : String? = null
    ElevatedCard(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = boxColorD,
        ),
        onClick = {
            Timber.v("SHOP_DETAILS")
            if((item.shopId?:"").isNotEmpty()){
                navController.navigate("$SHOP_DETAILS/${item.shopId}") {
                    popUpTo(TRANSECTION_HISTORY_COLLECTION) {
                        inclusive = false
                    }
                }
            } else{
                scope.launch {
                    message = "Unfortunately shop details are not available,please make home page's SEARCH useful"
                    snackbarHostState.showSnackbar(
                        message = message!!,
                        duration = SnackbarDuration.Short
                    )
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    "${item.firstName} ${item.lastName?:""}",
                    fontWeight = FontWeight(500),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = MaterialTheme.colorScheme.onSurface
                )
                item.shopName?.let {
                    Text(
                        it,
                        fontWeight = FontWeight(1),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    "Collected By : ${item.collectedBy}",
                    fontWeight = FontWeight(400),
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column {
                Row(Modifier.align(Alignment.End)) {
                    Text(
                        item.transactionType ?: "",
                        fontWeight = FontWeight(100),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = if (item.transactionType == "Credit") green else red,
                    )
                    Spacer(Modifier.width(20.dp))
                    Text(
                        (item.amount ?: 0L).absoluteValue.toString(),
                        fontWeight = FontWeight(500),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = if (item.transactionType == "Credit") green else red,
                    )
                }

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
    Spacer(Modifier.height(2.dp))
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                contentColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.surface,
                content = {
                    Text(
                        text = message?:""
                    )
                }
            )
        }
    )
}