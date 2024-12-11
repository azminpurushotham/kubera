package com.collection.kubera.ui.shopdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.states.ShopDetailUiState
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.headingLabelD
import com.collection.kubera.ui.theme.labelBackgroundD
import com.collection.kubera.ui.theme.labelD
import com.collection.kubera.ui.theme.red

@Preview
@Composable
fun ShopDetailsScreen(
    id: String? = null,
    viewModel: ShopDetailsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shop by viewModel.shop.collectAsState()
    var selectedOption by remember { mutableStateOf("Credit") } // Current selected option
    var balance by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(false) }

    fun enableButton() {
        isEnabled = balance.isNotEmpty() && balance.toLong() > 0
    }

    @Composable
    fun RadioButtonWithLabel(
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
        selectedColor: Color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            RadioButton(
                colors = RadioButtonDefaults.colors(selectedColor = selectedColor),
                selected = selected,
                onClick = onClick
            )
            Text(
                text = label,
                modifier = Modifier.clickable(onClick = onClick) // Allow clicking the label
            )
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.Start // Horizontally center items
    ) {

        when (uiState) {
            is ShopDetailUiState.Initial -> {
                id?.let { viewModel.getShopDetails(it) }
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = labelBackgroundD),
        ) {
            Text(
                shop?.shopName ?: "--",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Text(
            "Location",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )
        Text(
            text = shop?.location ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )

        Text(
            "Landmark",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )
        Text(
            text = shop?.landmark ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )
        Text(
            "Shop Availability",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )
        Text(
            text = if (shop?.status ?: false) {
                "Available"
            } else {
                "Not Available"
            },
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )

        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = labelBackgroundD),
        ) {
            Text(
                "Contact Details",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            "First Name",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )

        Text(
            text = shop?.firstName ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )

        Text(
            "Last Name",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )

        Text(
            text = shop?.lastName ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )

        Text(
            "Phone Number",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp)
        )

        Text(
            text = shop?.phoneNumber ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )

        Text(
            "Second Phone Number",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )

        Text(
            text = shop?.secondPhoneNumber ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )
        Text(
            "Mail Id",
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = labelD,
            modifier = Modifier.padding(start = 16.dp, top = 20.dp),
        )

        Text(
            text = shop?.mailId ?: "--",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            modifier = Modifier.padding(start = 16.dp),
            color = headingLabelD,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = labelBackgroundD),
        ) {
            Text(
                "Balance Amount : ${shop?.balance ?: 0}",
                color = if ((shop?.balance ?: 0) > 0) {
                    green
                } else {
                    red
                },
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButtonWithLabel(
                label = "Credit",
                selectedColor = green,
                selected = selectedOption == "Credit",
                onClick = {
                    selectedOption = "Credit"
                    enableButton()
                }
            )
            RadioButtonWithLabel(
                label = "Debit",
                selectedColor = red,
                selected = selectedOption == "Debit",
                onClick = {
                    selectedOption = "Debit"
                    enableButton()
                },
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = balance,
            onValueChange = {
                if (it.length <= 10) {
                    balance = it
                }
                enableButton()
            },
            label = { Text("Amount (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(start = 50.dp, end = 50.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                viewModel.updateBalance(
                    shop?.id,
                    balance,
                )
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            enabled = isEnabled, // Control button's enabled state
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface, // Green when enabled, Gray when disabled
                contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}


