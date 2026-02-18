package com.collection.kubera.ui.registration

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.collection.kubera.domain.model.User
import com.collection.kubera.states.RegistrationUiState
import com.collection.kubera.ui.login.LoginActivity
import com.collection.kubera.ui.theme.KuberaTheme
import com.collection.kubera.ui.theme.onHintD
import com.collection.kubera.ui.updatecredentials.UpdateCredentialActivity
import kotlinx.coroutines.flow.collectLatest

@Preview
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Select user") }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    val users by viewModel.users.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var password by remember { mutableStateOf("") }
    var isErrorPassword by rememberSaveable { mutableStateOf(false) }
    val passwordCharacterLimit = 8
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is RegistrationUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is RegistrationUiEvent.NavigateToUpdateCredentials -> {
                    val intent = Intent(context, UpdateCredentialActivity::class.java)
                    intent.putExtra("userCredentials", event.user)
                    context.startActivity(intent)
                }
            }
        }
    }

    fun validatePassword(password: String) {
        isErrorPassword = password.length < passwordCharacterLimit
    }

    fun enableButton() {
        isEnabled = !userName.contains("Select") && !isErrorPassword
    }

    KuberaTheme {
        Scaffold(
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (uiState) {
                        RegistrationUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                        else -> { /* Idle / Initial */ }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Registration", fontWeight = FontWeight(600), fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = { expanded = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                    ) {
                        Text(userName, color = MaterialTheme.colorScheme.onPrimary)
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(),
                            offset = DpOffset(x = 100.dp, y = 10.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            users.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.username) },
                                    onClick = {
                                        userName = user.username
                                        expanded = false
                                        selectedUser = user
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            if (it.length < 20) {
                                password = it.trim()
                                validatePassword(password)
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
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            viewModel.validateCredentials(userName, password)
                        },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                            contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Registration")
                    }
                    TextButton(
                        onClick = {
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            (context as? Activity)?.finish()
                        },
                        modifier = Modifier.align(alignment = Alignment.End),
                    ) {
                        Text("Go Back To Login", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        )
    }
}
