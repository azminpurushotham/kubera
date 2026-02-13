package com.collection.kubera.ui.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.headingLabelD
import com.collection.kubera.ui.theme.red
import kotlin.math.absoluteValue

@Composable
internal fun BalanceHeader(
    viewModel: ReportViewModel,
) {
    val balance by viewModel.balance.collectAsState()
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.surface,
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Total Collection",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight(600),
                color = headingLabelD,
            )
            Text(
                balance.absoluteValue.toString(),
                fontWeight = FontWeight(400),
                fontSize = 30.sp,
                color = if (balance > 0) green else red,
            )
        }
    }
}