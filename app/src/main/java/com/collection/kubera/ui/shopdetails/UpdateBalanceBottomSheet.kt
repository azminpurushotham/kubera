package com.collection.kubera.ui.shopdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.collection.kubera.data.Shop
import com.collection.kubera.ui.theme.boxColorD
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red


@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ShowBottomSheet(
    sheetState: SheetState,
    selectedOption: String,
    balance: String,
    isEnabled: Boolean,
    viewModel: ShopDetailsViewModel,
    shop: Shop?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetState = sheetState,
        containerColor = boxColorD,
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Confirmation",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    "Do you want to ${selectedOption.uppercase()} the balance with",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                Text(
                    balance,
                    color = if (selectedOption == "Credit") {
                        green
                    } else {
                        red
                    },
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight(600)
                )

                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = isEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == "Credit") {
                            green
                        } else {
                            red
                        },
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        viewModel.updateBalance(
                            shop?.id,
                            balance,
                            selectedOption
                        )
                        onDismiss()
                    }) {
                    Text(
                        if (selectedOption == "Credit") {
                            "Add Fund"
                        } else {
                            "Withdraw Fund"
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight(1),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        },
        onDismissRequest = {
            onDismiss()
        },
    )
}