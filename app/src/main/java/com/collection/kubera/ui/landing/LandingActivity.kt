package com.collection.kubera.ui.landing

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.collection.kubera.ui.theme.KuberaTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@AndroidEntryPoint
class LandingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            // Hide the system bars for full screen
            systemUiController.isSystemBarsVisible = false

            // Optionally set a background color
            systemUiController.setSystemBarsColor(
                color = Color.Transparent
            )
            KuberaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    LandingScreen()
                }
            }
        }
    }

}
