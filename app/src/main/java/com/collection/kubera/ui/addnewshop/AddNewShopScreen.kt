package com.collection.kubera.ui.addnewshop

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.NavHostController
import com.collection.kubera.states.AddNewShopUiState
import com.collection.kubera.ui.AllDestinations
import com.collection.kubera.ui.theme.headingLabelD
import com.collection.kubera.ui.theme.onHintD
import com.collection.kubera.utils.PreferenceHelper

@Preview
@Composable
fun AddNewShopScreen(
    navController: NavHostController? = null,
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
    var balance : String? by remember { mutableStateOf(null) }
    var isBalanceError by rememberSaveable { mutableStateOf(false) }
    var balanceError by rememberSaveable { mutableStateOf("") }
    var isLandmarkError by rememberSaveable { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneNumberError by rememberSaveable { mutableStateOf(false) }
    var isPhoneNumberFormatError by rememberSaveable { mutableStateOf(false) }
    var secondPhoneNumber by remember { mutableStateOf("") }
    var isSecondPhoneNumberError by rememberSaveable { mutableStateOf(false) }
    var isSecondaryPhoneNumberFormatError by rememberSaveable { mutableStateOf(false) }
    var mailId by remember { mutableStateOf("") }
    var isMailIdError by rememberSaveable { mutableStateOf(false) }
    var isMailIdFormateError by rememberSaveable { mutableStateOf(false) }
    val pref = PreferenceHelper.getPrefs(context)

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

    fun validatePhoneNumber() {
        isPhoneNumberError = (phoneNumber.length) != phoneNumberLimit
        try {
            phoneNumber.toLong()
            isPhoneNumberFormatError = false
        } catch (e: Exception) {
            isPhoneNumberFormatError = true
        }
    }

    fun validateSecondPhoneNumber() {
        if(secondPhoneNumber.isEmpty()){
            isSecondPhoneNumberError = false
            isSecondaryPhoneNumberFormatError = false
            return
        }
        isSecondPhoneNumberError = (secondPhoneNumber.length) != phoneNumberLimit
        try {
            secondPhoneNumber.toLong()
            isSecondaryPhoneNumberFormatError = false
        } catch (e: Exception) {
            isSecondaryPhoneNumberFormatError = true
        }
    }

    fun validateBalance() {
        try {
            balance?.toLong()
            balanceError = ""
            isBalanceError = false
        } catch (e: Exception) {
            balanceError = "Enter valid amount"
            isBalanceError = true
        }
    }


    fun validateEmail(mailId: String) {
        isMailIdFormateError = !Patterns.EMAIL_ADDRESS.matcher(mailId).matches()
    }

    fun enableButton() {
        isEnabled = !isShopNameError && shopName.isNotEmpty()
                && !isFirstNameError && firstName.isNotEmpty()
//                && !isLastNameError
//                && !isLocationError && location.isNotEmpty()
//                && !isLandmarkError
                && !isPhoneNumberError &&!isPhoneNumberFormatError && phoneNumber.isNotEmpty()
//                && !isSecondPhoneNumberError
//                && !isMailIdError
    }
    when (uiState) {
        is AddNewShopUiState.Initial -> {
            viewModel.setPreference(pref)
        }

        AddNewShopUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        is AddNewShopUiState.AddNewShopInit -> {

        }

        is AddNewShopUiState.AddNewShopSuccess -> {
            isEnabled = false
            Toast.makeText(context, (uiState as AddNewShopUiState.AddNewShopSuccess).outputText, Toast.LENGTH_SHORT).show()
            navController?.popBackStack(
                route = AllDestinations.SHOP_LIST,
                inclusive = false)
        }

        is AddNewShopUiState.AddNewShopError -> {
            Toast.makeText(context, (uiState as AddNewShopUiState.AddNewShopError).errorMessage, Toast.LENGTH_SHORT).show()
        }

        is AddNewShopUiState.AddNewShopCompleted ->{

        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
    ) {



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
                unfocusedLabelColor = onHintD,
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
                        text = "Min: ${shopName.length}/$characterLimit",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
//        OutlinedTextField(
//            value = location,
//            onValueChange = {
//                if (it.length <= nameLimit) {
//                    location = it
//                    location = location.replaceFirstChar { word -> word.uppercaseChar() }
//                    validateLocation(location)
//                }
//                enableButton()
//            },
//            label = { Text("Location") },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
//                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedLabelColor = onHintD,
//                cursorColor = MaterialTheme.colorScheme.onPrimary,
//            ),
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//            isError = isLocationError,
//            modifier = Modifier.fillMaxWidth(),
//            supportingText = {
//                if (isLocationError) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = "Limit: ${location.length}/$characterLimit",
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            },
//        )
//
//        OutlinedTextField(
//            value = landmark,
//            onValueChange = {
//                if (it.length <= nameLimit) {
//                    landmark = it
//                    landmark = landmark.replaceFirstChar { word -> word.uppercaseChar() }
//                    validateLandmark(landmark)
//                }
//                enableButton()
//            },
//            label = { Text("Landmark (optional)") },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
//                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedLabelColor = onHintD,
//                cursorColor = MaterialTheme.colorScheme.onPrimary,
//            ),
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//            isError = isLandmarkError,
//            modifier = Modifier.fillMaxWidth(),
//            supportingText = {
//                if (isLandmarkError) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = "Limit: ${landmark.length}/$characterLimit",
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            },
//        )

        OutlinedTextField(
            value = balance?:"",
            onValueChange = {
                if (it.length <= 10) {
                    balance = it
                }
                validateBalance()
            },
            label = { Text("Balance Amount (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = onHintD,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            isError = isBalanceError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isBalanceError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = balanceError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        Spacer(Modifier.height(30.dp))
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
                unfocusedLabelColor = onHintD,
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
                        text = "Min: ${firstName.length}/$characterLimit",
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
                unfocusedLabelColor = onHintD,
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
                        text = "Min: ${lastName.length}/$characterLimit",
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
                        validatePhoneNumber()
                    }
                }
                enableButton()
            },
            label = { Text("Phone Number") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = onHintD,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isPhoneNumberError || isPhoneNumberFormatError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isPhoneNumberError || isPhoneNumberFormatError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text =  if(isPhoneNumberFormatError) "Invalid phone number" else if(isPhoneNumberError) "Min: ${phoneNumber.length}/$phoneNumberLimit" else "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = secondPhoneNumber,
            onValueChange = {
                if (it.length <= phoneNumberLimit) {
                    secondPhoneNumber = it
                    validateSecondPhoneNumber()
                }
                enableButton()
            },
            label = { Text("Secondary Phone Number (optional)") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = onHintD,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isSecondPhoneNumberError || isSecondaryPhoneNumberFormatError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isSecondPhoneNumberError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text =  if(isSecondaryPhoneNumberFormatError) "Invalid phone number" else if(isSecondPhoneNumberError) "Min: ${secondPhoneNumber.length}/$phoneNumberLimit" else "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
//        OutlinedTextField(
//            value = mailId,
//            onValueChange = {
//                if (it.length <= 50) {
//                    mailId = it.trim()
//                    validateEmail(mailId)
//                }
//                enableButton()
//            },
//            label = { Text("Mail Id (optional)") },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
//                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedLabelColor = onHintD,
//                cursorColor = MaterialTheme.colorScheme.onPrimary,
//            ),
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            isError = isMailIdError||isMailIdFormateError,
//            modifier = Modifier.fillMaxWidth(),
//            supportingText = {
//                if (isMailIdError) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = "Limit: ${mailId.length}/$characterLimit",
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//               else if (isMailIdFormateError) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = "Email not valid",
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            },
//        )
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
                containerColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, // Green when enabled, Gray when disabled
                contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Save")
        }
    }
}


