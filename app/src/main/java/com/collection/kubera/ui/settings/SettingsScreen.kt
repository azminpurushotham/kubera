package com.collection.kubera.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.collection.kubera.states.SettingsUiState

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState(SettingsUiState.Initial)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.Start // Horizontally center items
    ) {

    }

}