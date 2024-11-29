package com.collection.kubera.ui.updatecredentials

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.states.UpdateCredentialsUiState
import com.collection.kubera.ui.theme.KuberaTheme

@Preview
@Composable
fun UpdateCredentialsScreen(viewModel :UpdateCredentialsViewModel = viewModel<UpdateCredentialsViewModel>()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var userName by remember { mutableStateOf("") }
    var isErrorUserName by rememberSaveable { mutableStateOf(false) }
    val userNameCharacterLimit = 5

    var password by remember { mutableStateOf("") }
    var isErrorPassword by rememberSaveable { mutableStateOf(false) }
    val passwordCharacterLimit = 8
    var passwordVisible by remember { mutableStateOf(false) }

    var confirmPassword by remember { mutableStateOf("") }
    var isErrorConfirmPassword by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isEnabled by remember { mutableStateOf(false) }

    fun validateUserName(userName: String) {
        isErrorUserName = userName.length < userNameCharacterLimit
    }

    fun validatePassword(password: String) {
        isErrorPassword = password.length < passwordCharacterLimit
    }

    fun validateConfirmPassword(password: String) {
        isErrorConfirmPassword = confirmPassword.length < passwordCharacterLimit
    }

    fun enableButton() {
        isEnabled = !isErrorUserName && !isErrorPassword
    }

    when (uiState) {
        is UpdateCredentialsUiState.Initial -> {

        }

        is UpdateCredentialsUiState.Loading -> {

        }

        is UpdateCredentialsUiState.UserCredentials->{
            userName = (uiState as UpdateCredentialsUiState.UserCredentials).user.username
        }

        is UpdateCredentialsUiState.UpdationSuccess -> {
            Toast.makeText(
                context,
                (uiState as UpdateCredentialsUiState.UpdationSuccess).message,
                Toast.LENGTH_LONG
            ).show()
        }

        is UpdateCredentialsUiState.UpdationFiled -> {
            Toast.makeText(
                context,
                (uiState as UpdateCredentialsUiState.UpdationFiled).message,
                Toast.LENGTH_LONG
            ).show()
        }

        is UpdateCredentialsUiState.ConfirmPasswordError ->{}
        is UpdateCredentialsUiState.PasswordError -> {}
        is UpdateCredentialsUiState.PasswordMismatchError -> {}
        is UpdateCredentialsUiState.UserNameError -> {}
    }

    KuberaTheme {
        Scaffold(
            content = { paddingValues ->
                // Page content goes here
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center, // Vertically center items
                    horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
                ) {
                    Text(
                        "Change Credentials",
                        fontWeight = FontWeight(600),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Please update your username and password",
                        fontWeight = FontWeight(300),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    OutlinedTextField(
                        value = userName,
                        onValueChange = {
                            if (it.length < 20) {
                                userName = it
                                validateUserName(userName)
                            }
                            enableButton()
                        },
                        label = { Text("User Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = isErrorUserName,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (isErrorUserName) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Limit: ${userName.length}/$userNameCharacterLimit",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp)) // Adds 16dp space
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            if (it.length < 20) {
                                password = it
                                validatePassword(password)
                            }
                            enableButton()
                        },
                        label = { Text("Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = isErrorPassword,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        supportingText = {
                            if (isErrorPassword) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Limit: ${password.length}/$passwordCharacterLimit",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            if (it.length < 20) {
                                confirmPassword = it
                                validateConfirmPassword(confirmPassword)
                            }
                            enableButton()
                        },
                        label = { Text("Confirm Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = isErrorConfirmPassword,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        supportingText = {
                            if (isErrorConfirmPassword) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Limit: ${confirmPassword.length}/$passwordCharacterLimit",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (uiState is UpdateCredentialsUiState.PasswordMismatchError) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = (uiState as UpdateCredentialsUiState.PasswordMismatchError).message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            viewModel.updateCredentials(
                                userName,
                                password,
                                confirmPassword
                            )
                        },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEnabled, // Control button's enabled state
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary, // Green when enabled, Gray when disabled
                            contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        )
    }


}




