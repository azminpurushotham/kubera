package com.collection.kubera.ui.shoplist
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.collection.kubera.data.Shop
import com.collection.kubera.ui.AllDestinations.SHOP_DETAILS
import com.collection.kubera.ui.AllDestinations.SHOP_LIST
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red
import com.google.gson.Gson
import timber.log.Timber
import kotlin.math.absoluteValue

@Composable
internal fun ShopItem(
    navController: NavHostController,
    item: Shop
) {
    ElevatedCard(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = boxColorD,
        ),
        onClick = {
            Timber.i("SHOP_DETAILS")
            navController.navigate("$SHOP_DETAILS?${Gson().toJson(item)}") {
                popUpTo(SHOP_LIST) {
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
                    ((item.balance ?: 0L).absoluteValue).toString(),
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