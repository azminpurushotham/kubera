package com.collection.kubera.ui.addnewshop

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.states.AddNewShopUiState
import com.collection.kubera.ui.theme.headingLabelD

@Preview
@Composable
fun AddNewShopScreen(
    viewModel: AddNewShopViewModel = viewModel()
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
        OutlinedTextField(
            value = shopName,
            onValueChange = {
                if (shopName.length <= nameLimit) {
                    shopName = it
                    shopName = shopName.replaceFirstChar { word -> word.uppercaseChar() }
                    validateShopName(shopName)
                }
                enableButton()
            },
            label = { Text("Shop Name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isShopNameError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isShopNameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${shopName.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
        OutlinedTextField(
            value = location,
            onValueChange = {
                if (it.length <= nameLimit) {
                    location = it
                    location = location.replaceFirstChar { word -> word.uppercaseChar() }
                    validateLocation(location)
                }
                enableButton()
            },
            label = { Text("Location") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isLocationError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isLocationError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${location.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = landmark,
            onValueChange = {
                if (it.length <= nameLimit) {
                    landmark = it
                    landmark = landmark.replaceFirstChar { word -> word.uppercaseChar() }
                    validateLandmark(landmark)
                }
                enableButton()
            },
            label = { Text("Landmark (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isLandmarkError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isLandmarkError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${landmark.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = balance,
            onValueChange = {
                if (it.length <= 10) {
                    balance = it
                }
            },
            label = { Text("Balance Amount (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(60.dp))
        Text(
            "Enter Owner Details",
            fontSize = 18.sp,
            fontWeight = FontWeight(1),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                if (it.length <= nameLimit) {
                    firstName = it.trim()
                    firstName = firstName.replaceFirstChar { word -> word.uppercaseChar() }
                    validateFirstName(firstName)
                }
                enableButton()
            },
            label = { Text("First Name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isFirstNameError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isFirstNameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${firstName.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = {
                if (it.length <= nameLimit) {
                    lastName = it.trim()
                    lastName = lastName.replaceFirstChar { word -> word.uppercaseChar() }
                    validateLastName(lastName)
                }
                enableButton()
            },
            label = { Text("Last Name (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isLastNameError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isLastNameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${lastName.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )


        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.length <= phoneNumberLimit) {
                    phoneNumber = it.trim()
                    if (phoneNumber.isNotEmpty()) {
                        validatePhoneNumber(phoneNumber.toLong())
                    }
                }
                enableButton()
            },
            label = { Text("Phone Number") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isPhoneNumberError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isPhoneNumberError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${(phoneNumber ?: 0).toString().length}/$phoneNumberLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = secondPhoneNumber,
            onValueChange = {
                if (it.length <= phoneNumberLimit) {
                    secondPhoneNumber = it.trim()
                    if (secondPhoneNumber.isNotEmpty()) {
                        validateSecondPhoneNumber(secondPhoneNumber.toLong())
                    }
                }
                enableButton()
            },
            label = { Text("Secondary Phone Number (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isSecondPhoneNumberError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isSecondPhoneNumberError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${(secondPhoneNumber ?: 0).toString().length}/$phoneNumberLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
        OutlinedTextField(
            value = mailId,
            onValueChange = {
                if (it.length <= 50) {
                    mailId = it.trim()
                    validateEmail(mailId)
                }
                enableButton()
            },
            label = { Text("Mail Id (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = isMailIdError||isMailIdFormateError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isMailIdError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Limit: ${mailId.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
               else if (isMailIdFormateError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Email not valid",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
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
                    if(secondPhoneNumber.isNotEmpty()) secondPhoneNumber else null,
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


