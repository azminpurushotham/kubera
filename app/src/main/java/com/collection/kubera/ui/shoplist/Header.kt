package com.collection.kubera.ui.shoplist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.onHintD
import com.collection.kubera.ui.theme.red


@Composable
internal fun Header(
    shopName: String,
    viewModel: ShopListViewModel,
    todaysCollection: Long,
    todaysCredit: Long,
    todaysDebit: Long,
    balance: Long
) {
    var shopName1 = shopName
    Spacer(modifier = Modifier.height(15.dp))
    Box {
        OutlinedTextField(
            value = shopName1,
            onValueChange = {
                shopName1 = it
                viewModel.getShops(shopName1)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = onHintD,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            label = { Text("Search shops here...") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 0.dp),
            trailingIcon = {
                val icon = Icons.Filled.Search
                IconButton(onClick = {
                    viewModel.getShops(shopName1)
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Today's Collection",
                    fontWeight = FontWeight(600),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row {
                    Text(
                        todaysCollection.toString(),
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = if (todaysCollection > 0) green else red,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        todaysCredit.toString(),
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = green,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        todaysDebit.toString(),
                        fontWeight = FontWeight(400),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
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
    Spacer(modifier = Modifier.height(10.dp))
}