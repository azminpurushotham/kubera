package com.collection.kubera.ui.shopdetails

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.states.AddNewShopUiState
import com.collection.kubera.ui.theme.headingLabelD

@Preview
@Composable
fun ShopDetailsScreen(
    viewModel: ShopDetailsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var isEnabled by remember { mutableStateOf(false) }
    val characterLimit = 3
    val phoneNumberLimit = 10
    val nameLimit = 30
    var firstName by remember { mutableStateOf("") }
    var isFirstNameError by rememberSaveable { mutableStateOf(false) }
    var lastName by remember { mutableStateOf("") }
    var isLastNameError by rememberSaveable { mutableStateOf(false) }
    var shopName by remember { mutableStateOf("") }
    var isShopNameError by rememberSaveable { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var isLocationError by rememberSaveable { mutableStateOf(false) }
    var landmark by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var isLandmarkError by rememberSaveable { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneNumberError by rememberSaveable { mutableStateOf(false) }
    var secondPhoneNumber by remember { mutableStateOf("") }
    var isSecondPhoneNumberError by rememberSaveable { mutableStateOf(false) }
    var mailId by remember { mutableStateOf("") }
    var isMailIdError by rememberSaveable { mutableStateOf(false) }
    var isMailIdFormateError by rememberSaveable { mutableStateOf(false) }


    fun validateShopName(shopName: String) {
        isShopNameError = shopName.length < characterLimit
    }

    fun validateFirstName(firstName: String) {
        isFirstNameError = firstName.length < characterLimit
    }

    fun validateLastName(lastName: String) {
        isLastNameError = lastName.length < characterLimit
    }

    fun validateLocation(location: String) {
        isLocationError = location.length < characterLimit
    }

    fun validateLandmark(landmark: String) {
        isLandmarkError = landmark.length < characterLimit
    }

    fun validatePhoneNumber(phoneNumber: Long?) {
        isPhoneNumberError = phoneNumber != null
        isPhoneNumberError = ((phoneNumber ?: 0).toString().length) != phoneNumberLimit
    }

    fun validateSecondPhoneNumber(secondPhoneNumber: Long?) {
        isSecondPhoneNumberError = secondPhoneNumber != null
        isSecondPhoneNumberError = (secondPhoneNumber ?: 0).toString().length != phoneNumberLimit
    }

    fun validateEmail(mailId: String) {
        isMailIdFormateError = !Patterns.EMAIL_ADDRESS.matcher(mailId).matches()
    }

    fun enableButton() {
        isEnabled = !isShopNameError && shopName.isNotEmpty()
                && !isFirstNameError && firstName.isNotEmpty()
//                && !isLastNameError
                && !isLocationError && location.isNotEmpty()
//                && !isLandmarkError
                && !isPhoneNumberError && phoneNumber.isNotEmpty()
//                && !isSecondPhoneNumberError
//                && !isMailIdError
    }


    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
    ) {

        when (uiState) {
            is AddNewShopUiState.Initial -> {

            }

            AddNewShopUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            is AddNewShopUiState.AddNewShopInit -> {

            }

            is AddNewShopUiState.AddNewShopSuccess -> {
            }

            is AddNewShopUiState.AddNewShopError -> {

            }
        }

        Text(
            "Enter Shop Details",
            fontSize = 18.sp,
            fontWeight = FontWeight(1),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = shopName,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = headingLabelD,
        )
        Text(
            text = location,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = landmark,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = balance,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(60.dp))
        Text(
            "Enter Owner Details",
            fontSize = 18.sp,
            fontWeight = FontWeight(1),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = firstName,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = lastName,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = phoneNumber,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = secondPhoneNumber,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = mailId,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                viewModel.saveShopDetails(
                    shopName,
                    location,
                    landmark,
                    balance,
                    firstName,
                    lastName,
                    phoneNumber,
                    if (secondPhoneNumber.isNotEmpty()) secondPhoneNumber else null,
                    mailId
                )
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled, // Control button's enabled state
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface, // Green when enabled, Gray when disabled
                contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Save")
        }
    }
}


