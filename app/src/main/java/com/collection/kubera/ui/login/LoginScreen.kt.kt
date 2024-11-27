package com.collection.kubera.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.ui.theme.KuberaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    var userName by remember { mutableStateOf("") }
    var isErrorUserName by rememberSaveable { mutableStateOf(false) }
    val userNameCharacterLimit = 5

    fun validateUserName(userName: String) {
        isErrorUserName = userName.length < userNameCharacterLimit
    }
    KuberaTheme {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Login") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    },
        content = { paddingValues ->
            // Page content goes here
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center, // Vertically center items
                horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it
                        validateUserName(userName)},
                    label = { Text("User Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = isErrorUserName,
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
            }
        }
    )
        }


}
