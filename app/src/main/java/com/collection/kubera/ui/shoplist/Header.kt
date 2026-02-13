package com.collection.kubera.ui.shoplist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.collection.kubera.ui.theme.onHintD


@Composable
internal fun Header(
    viewModel: ShopListViewModel
) {
    var shopName by remember { mutableStateOf("") }
    Spacer(modifier = Modifier.height(15.dp))
    Box {
        OutlinedTextField(
            value = shopName,
            onValueChange = {
                shopName = it
                viewModel.onSearchQueryChanged(shopName)
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
                    viewModel.onSearchQueryChanged(shopName)
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Search"
                    )
                }
            },
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    BalanceHeader(viewModel)
    Spacer(modifier = Modifier.height(10.dp))
}
