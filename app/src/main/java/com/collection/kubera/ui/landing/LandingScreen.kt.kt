package com.collection.kubera.ui.landing

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.collection.kubera.ui.login.LoginActivity
import com.collection.kubera.ui.main.MainActivity
import com.collection.kubera.utils.PreferenceHelper

@Preview
@Composable
fun LandingScreen(
    landingViewModel: LandingViewModel = viewModel()
) {
    val context = LocalContext.current
    val pref = PreferenceHelper.getPrefs(context)
    val isLoggedIn = landingViewModel.checkLoginStatus(pref)

    if(isLoggedIn){
        context.startActivity(Intent(context, MainActivity::class.java))
        val currentActivity = context as? LandingActivity
        currentActivity?.finish()
    }else{
        context.startActivity(Intent(context, LoginActivity::class.java))
        val currentActivity = context as? LandingActivity
        currentActivity?.finish()
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}