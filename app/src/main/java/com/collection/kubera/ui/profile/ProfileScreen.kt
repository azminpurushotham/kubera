package com.collection.kubera.ui.profile

import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.ui.login.LoginActivity
import com.collection.kubera.ui.theme.KuberaTheme
import com.collection.kubera.ui.theme.onHintD
import com.collection.kubera.utils.PreferenceHelper
import com.collection.kubera.utils.USER_ID


@Preview
@Composable
fun ProfileScreen(viewModel :ProfileViewModel = viewModel<ProfileViewModel>()) {
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
    var isErrorPasswordMismatch by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isEnabled by remember { mutableStateOf(false) }
    val pref = PreferenceHelper.getPrefs(context)

    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.PackageInfoFlags.of(0)
        )
    } else {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }

    val versionName  by remember { mutableStateOf(packageInfo.versionName)}
    val versionCode by remember { mutableStateOf(packageInfo.longVersionCode)}
    val appName  = context.applicationInfo.loadLabel(context.packageManager).toString()

    fun validateUserName() {
        isErrorUserName = userName.length < userNameCharacterLimit
    }

    fun validatePassword() {
        isErrorPassword = password.length < passwordCharacterLimit
    }

    fun validateConfirmPassword() {
        isErrorConfirmPassword = confirmPassword.length < passwordCharacterLimit
        isErrorPasswordMismatch = (!isErrorConfirmPassword && (password != confirmPassword))
        if (isErrorPasswordMismatch) {
            viewModel.setPasswordMismatchError()
        }
    }

    fun enableButton() {
        isEnabled = !isErrorUserName && !isErrorPassword && !isErrorPasswordMismatch
    }

    when (uiState) {
        is ProfileUiState.Initial -> {
            pref.getString(USER_ID,"")?.let {id->
                viewModel.getUserDetails(id)
            }
        }

        is ProfileUiState.Loading -> {

        }

        is ProfileUiState.UserCredentials->{
            userName = (uiState as ProfileUiState.UserCredentials).user.username
            password = (uiState as ProfileUiState.UserCredentials).user.password
        }

        is ProfileUiState.UpdationSuccess -> {
            Toast.makeText(
                context,
                (uiState as ProfileUiState.UpdationSuccess).message,
                Toast.LENGTH_LONG
            ).show()
        }

        is ProfileUiState.UpdationFiled -> {
            Toast.makeText(
                context,
                (uiState as ProfileUiState.UpdationFiled).message,
                Toast.LENGTH_LONG
            ).show()
        }

        is ProfileUiState.ConfirmPasswordError ->{}
        is ProfileUiState.PasswordError -> {}
        is ProfileUiState.PasswordMismatchError -> {}
        is ProfileUiState.UserNameError -> {}
        is ProfileUiState.Error -> Toast.makeText(
            context,
            (uiState as ProfileUiState.Error).message,
            Toast.LENGTH_LONG
        ).show()
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
                    verticalArrangement = Arrangement.Top, // Vertically center items
                    horizontalAlignment = Alignment.Start // Horizontally center items
                ) {
                    Text(
                        "User Details",
                        fontWeight = FontWeight(600),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = userName,
                        onValueChange = {
                            if (it.length < 20) {
                                userName = it.trim()
                                validateUserName()
                            }
                            enableButton()
                        },
                        label = { Text("User Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = onHintD,
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
                                password = it.trim()
                                validatePassword()
                            }
                            enableButton()
                        },
                        label = { Text("Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = onHintD,
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
                                confirmPassword = it.trim()
                                validateConfirmPassword()
                            }
                            enableButton()
                        },
                        label = { Text("Confirm Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = onHintD,
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
                            } else if (isErrorPasswordMismatch) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = (uiState as ProfileUiState.PasswordMismatchError).message,
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
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        text = "$appName : $versionCode $versionName",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}




