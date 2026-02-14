package com.collection.kubera.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.collection.kubera.ui.theme.KuberaTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    @Preview
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val viewModel: LoginViewModel = hiltViewModel()
                    val googleSignInLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        val data = result.data
                        if (result.resultCode == Activity.RESULT_OK && data != null) {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                            try {
                                val account = task.getResult(ApiException::class.java)
                                account?.idToken?.let { idToken ->
                                    viewModel.signInWithGoogle(idToken)
                                } ?: viewModel.showError("Google sign-in was cancelled")
                            } catch (e: ApiException) {
                                viewModel.showError(e.status?.statusMessage ?: "Google sign-in failed")
                            }
                        }
                    }
                    LoginScreen(
                        viewModel = viewModel,
                        onLaunchGoogleSignIn = { intent ->
                            googleSignInLauncher.launch(intent)
                        }
                    )
                }
            }
        }
    }
}