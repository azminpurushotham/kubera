package com.collection.kubera.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.collection.kubera.ui.AppNavGraph
import com.collection.kubera.ui.theme.KuberaTheme
import com.collection.kubera.ui.theme.primaryD

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContent {
            KuberaTheme {
                AppNavGraph()
            }
        }
    }

    private fun setStatusBarColor() {
        // Enable edge-to-edge rendering
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set status bar color
        val statusBarColor = primaryD // Replace with your theme color
        window.statusBarColor = statusBarColor.toArgb()
        // Adjust system bar icon color (light or dark)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    }
}