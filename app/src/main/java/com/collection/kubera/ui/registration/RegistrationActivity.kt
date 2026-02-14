package com.collection.kubera.ui.registration

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.collection.kubera.ui.theme.KuberaTheme

@AndroidEntryPoint
class RegistrationActivity : ComponentActivity() {
    @Composable
    fun StatusBarWithPlatformApi() {
        val activity = LocalContext.current as Activity
        val view = LocalView.current
        val statusBarColor = MaterialTheme.colorScheme.primary // Color from color scheme
        val darkIcons = MaterialTheme.colorScheme.primary.luminance() > 0.5f

        activity.window.statusBarColor = statusBarColor.toArgb() // Set status bar color
        WindowInsetsControllerCompat(activity.window, view).isAppearanceLightStatusBars = darkIcons // Adjust icon color
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KuberaTheme {
                StatusBarWithPlatformApi()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    RegistrationScreen()
                }
            }
        }
    }
}