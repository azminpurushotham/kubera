package com.collection.kubera.ui.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.collection.kubera.data.User
import com.collection.kubera.states.HomeUiState
import com.collection.kubera.ui.theme.KuberaTheme
import com.collection.kubera.ui.theme.green
import com.collection.kubera.ui.theme.red
import com.collection.kubera.ui.updatecredentials.UpdateCredentialActivity

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) } // Tracks whether the menu is open
    var isEnabled by remember { mutableStateOf(false) } // Tracks whether the menu is open
    var userName by remember { mutableStateOf("Select user") } // Tracks selected item
    val users by viewModel.users.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var selectedUser by remember { mutableStateOf<User?>(null) }

    var shopName by remember { mutableStateOf("") }
    var isErrorPassword by rememberSaveable { mutableStateOf(false) }
    val passwordCharacterLimit = 8
    var passwordVisible by remember { mutableStateOf(false) }

    fun validatePassword(password: String) {
        isErrorPassword = password.length < passwordCharacterLimit
    }

    fun enableButton() {
        isEnabled = !userName.contains("Select") && !isErrorPassword
    }

    val colorItems = listOf(green, red)

    viewModel.getUsers()

    KuberaTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Shop List", fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                        }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
            content = { paddingValues ->
                // Page content goes here
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center, // Vertically center items
                    horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
                ) {

                    when (uiState) {
                        is HomeUiState.Initial -> {

                        }

                        HomeUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }

                        is HomeUiState.HomeInit -> {

                        }

                        is HomeUiState.HomeSuccess -> {
                            val intent = Intent(context, UpdateCredentialActivity::class.java)
                            intent.apply {
                                putExtra("userCredentials", selectedUser)
                            }
                            context.startActivity(intent)
                        }

                        is HomeUiState.HomeError -> {
                            Toast.makeText(
                                context,
                                (uiState as HomeUiState.HomeError).errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    OutlinedTextField(
                        value = shopName,
                        onValueChange = {
                            shopName = it
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = isErrorPassword,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                            .height(40.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =Icons.Filled.Search
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "9:00 am",
                                    fontWeight = FontWeight(600), fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "1-Dec-2024",
                                    fontWeight = FontWeight(400), fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                "1000", fontWeight = FontWeight(400),
                                fontSize = 30.sp,
                                color = green,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    for (item in 0..20) {
                        Row(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "User Name $item",
                                    fontWeight = FontWeight(400), fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "Shop Name $item",
                                    fontWeight = FontWeight(400), fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Column {
                                Text(
                                    "1000", fontWeight = FontWeight(500),
                                    fontSize = 15.sp,
                                    color = colorItems.random(),
                                )
                                Text(
                                    "1-Dec-2024 10:00 am",
                                    fontWeight = FontWeight(400), fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                }
            }
        )
    }
}



