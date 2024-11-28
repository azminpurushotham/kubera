package com.collection.kubera.ui.registration

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.ui.theme.KuberaTheme

@Preview
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) } // Tracks whether the menu is open
    var isEnabled by remember { mutableStateOf(false) } // Tracks whether the menu is open
    var selectedText by remember { mutableStateOf("Select an item") } // Tracks selected item


    KuberaTheme {
        Scaffold(/*topBar = {
        TopAppBar(
            title = { Text("Login") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    },*/
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
                    Text("Registration", fontWeight = FontWeight(600), fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton (
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            expanded = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                    ) {
                        Text("Select user", color = MaterialTheme.colorScheme.onPrimary)
                    }

                    DropdownMenu(
                        expanded = expanded, // Controls visibility
                        onDismissRequest = { expanded = false } // Close when clicking outside
                    ) {
                        DropdownMenuItem(
                            text = { Text("Option 1") },
                            onClick = {
                                selectedText = "Option 1"
                                expanded = false // Close menu after selection
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Option 2") },
                            onClick = {
                                selectedText = "Option 2"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Option 3") },
                            onClick = {
                                selectedText = "Option 3"
                                expanded = false
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEnabled, // Control button's enabled state
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary, // Green when enabled, Gray when disabled
                            contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Registration")
                    } 
                    TextButton(
                        onClick = {},
                        modifier = Modifier.align(alignment = Alignment.End),
                    ) {
                        Text("Login", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        )
    }


}



