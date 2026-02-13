package com.collection.kubera

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.collection.kubera.ui.login.LoginActivity
import org.junit.Rule
import org.junit.Test

class LoginTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testLoginActivity(){
        rule.setContent {
            LoginActivity()
        }

    }
}