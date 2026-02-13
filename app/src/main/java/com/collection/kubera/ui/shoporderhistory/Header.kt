package com.collection.kubera.ui.shoporderhistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.collection.kubera.R
import com.collection.kubera.data.Shop
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.onprimaryD
import com.collection.kubera.ui.theme.red
import kotlin.math.absoluteValue


@Composable
internal fun Header(shop: Shop?, balance: Long) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
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
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "${shop?.firstName} ${shop?.lastName}",
                fontWeight = FontWeight(600),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                "${shop?.shopName}",
                fontWeight = FontWeight(400),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(
            balance.absoluteValue.toString(),
            fontWeight = FontWeight(400),
            fontSize = 30.sp,
            color = if (balance > 0) {
                green
            } else {
                red
            },
        )
    }
}