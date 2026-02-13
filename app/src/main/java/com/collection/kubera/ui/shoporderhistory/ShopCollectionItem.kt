package com.collection.kubera.ui.shoporderhistory

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red
import kotlin.math.absoluteValue


@Composable
internal fun ShopCollectionItem(item: CollectionModel) {
    ElevatedCard(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = boxColorD,
        ),
        onClick = {

        }) {
        Column(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
                .align(Alignment.Start)
                .fillMaxWidth(),){
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
            ) {
                Row (
                    modifier = Modifier.weight(1.5f),
                    horizontalArrangement = Arrangement.Start
                    ){
                    Text(
                        item.datedmy,
                        fontWeight = FontWeight(100),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        item.time,
                        fontWeight = FontWeight(100),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    item.transactionType ?: "",
                    fontWeight = FontWeight(100),
                    modifier = Modifier.weight(.5f),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = if (item.transactionType == "Credit") green else red,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    ((item.amount ?: 0L).absoluteValue).toString(),
                    fontWeight = FontWeight(500),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = if (item.transactionType == "Credit") green else red,
                )
            }
            Row {
                Text(
                    "Collected By : ${item.collectedBy}",
                    fontWeight = FontWeight(400),
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}