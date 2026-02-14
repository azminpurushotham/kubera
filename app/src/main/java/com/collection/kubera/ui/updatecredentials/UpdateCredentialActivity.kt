package com.collection.kubera.ui.updatecredentials

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import com.collection.kubera.data.User
import com.collection.kubera.ui.theme.KuberaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateCredentialActivity : ComponentActivity() {

    @Composable
    fun StatusBarWithPlatformApi() {
        val activity = LocalContext.current as Activity
        val view = LocalView.current
        val statusBarColor = MaterialTheme.colorScheme.primary
        val darkIcons = MaterialTheme.colorScheme.primary.luminance() > 0.5f

        activity.window.statusBarColor = statusBarColor.toArgb()
        WindowInsetsControllerCompat(activity.window, view).isAppearanceLightStatusBars = darkIcons
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userCredentials: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("userCredentials", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("userCredentials")
        }
        setContent {
            KuberaTheme {
                StatusBarWithPlatformApi()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    userCredentials?.let { user ->
                        UpdateCredentialsScreen(user = user)
                    }
                }
            }
        }
    }
}
