package com.collection.kubera.ui.shoplist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red

@Composable
internal fun BalanceHeader(
    viewModel: ShopListViewModel
) {
    val balance by viewModel.balance.collectAsState()
    val todaysCredit by viewModel.todaysCredit.collectAsState()
    val todaysDebit by viewModel.todaysDebit.collectAsState()
    val todaysCollection by viewModel.todaysCollection.collectAsState()
    Card(
        elevation = CardDefaults.cardElevation(20.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .background(color = boxColorD)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Today's Collection",
                    fontWeight = FontWeight(600),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.Start, // Aligns items to the left
                    verticalAlignment = Alignment.Bottom // Aligns items to the bottom
                ) {
                    Text(
                        todaysCollection.toString(),
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = if (todaysCollection > 0) green else red,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Cr:",
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = green,
                    )
                    Text(
                        "$todaysCredit",
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = green,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Dr:",
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = red,
                    )
                    Text(
                        "$todaysDebit",
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = red,
                    )
                }
            }
            Text(
                balance.toString(),
                fontWeight = FontWeight(400),
                fontSize = 30.sp,
                color = if (balance > 0) green else red,
            )
        }
    }
}