package com.collection.kubera

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.collection.kubera.ui.addnewshop.AddNewShopScreen
import org.junit.Rule
import org.junit.Test

class AddNewShopTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testAddNewShop() {
        rule.setContent { AddNewShopScreen() }
        rule.onNodeWithText("Enter Shop Details").assertIsDisplayed()
        rule.onNodeWithText("Shop Name").assertIsDisplayed()
        rule.onNodeWithText("Balance Amount (optional)").assertIsDisplayed()
        rule.onNodeWithText("Enter Owner Details").assertIsDisplayed()
        rule.onNodeWithText("First Name").assertIsDisplayed()
        rule.onNodeWithText("Last Name (optional)").assertIsDisplayed()
        rule.onNodeWithText("Phone Number").assertIsDisplayed()
        rule.onNodeWithText("Secondary Phone Number (optional)").assertIsDisplayed()
        rule.onNodeWithText("Save").assertIsDisplayed()
        rule.onNode(
            hasText("Save")
                    and hasClickAction()
                    and !isEnabled()
        )
      }

    @Test
    fun testAddNewShopEntries(){
        rule.setContent { AddNewShopScreen() }


    }

}