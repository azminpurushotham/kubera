package com.collection.kubera
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.collection.kubera.ui.addnewshop.AddNewShopScreen
import org.junit.Rule
import org.junit.Test

class AddNewShopTest {


    private val shopnameN = hasText("Shop Name")
    private val balanceN = hasText("Balance Amount (optional)")
    private val firstNameN = hasText("First Name")
    private val lastNameN = hasText("Last Name (optional)")
    private val phoneNumberN = hasText("Phone Number")
    private val secondaryPhoneNumberN = hasText("Secondary Phone Number (optional)")
    private val saveN = hasText("Save")

    private val shopnameP = "Test Shop Name"
    private val balanceP = "7895624"
    private val firstNameP = "Test First Name"
    private val lastNameP = "Test Last Name"
    private val phoneNumberP = "8525856459"
    private val secondaryPhoneNumberP = "8542589632"

    private val shopname = "T"
    private val balance = "7895624"
    private val firstName = "Test First Name"
    private val lastName = "Test Last Name"
    private val phoneNumber = "8525856459"
    private val secondaryPhoneNumber = "8542589632"

    private val lengthError1 = "Min"
    private val lengthError2 = "Limit"
    private val balanceError = "valid amount"
    private val phoneError1 = "Invalid"
    private val phoneError2 = "Invalid"


    @get:Rule
    val rule = createComposeRule()

    /* Initial loading testing*/
    @Test
    fun init() {
        rule.setContent { AddNewShopScreen() }
        rule.onNode(shopnameN).assertIsDisplayed()
        rule.onNode(balanceN).assertIsDisplayed()
        rule.onNodeWithText("Enter Owner Details").assertIsDisplayed()
        rule.onNode(firstNameN).assertIsDisplayed()
        rule.onNode(lastNameN).assertIsDisplayed()
        rule.onNode(phoneNumberN).assertIsDisplayed()
        rule.onNode(secondaryPhoneNumberN).assertIsDisplayed()
        rule.onNode(saveN).assertIsDisplayed()
        rule.onNode(
            saveN
                    and hasClickAction()
                    and !isEnabled()
        ).assertIsDisplayed()
      }

    @Test
    fun testPositiveEntries(){
        rule.setContent { AddNewShopScreen() }
        rule.onNode(shopnameN).performTextInput(shopnameP)
        rule.onNode(
            hasText(shopnameP)
        ).assertIsDisplayed()
        rule.onNode(balanceN).performTextInput(balanceP)
        rule.onNode(
            hasText(balanceP)
        ).assertIsDisplayed()
        rule.onNode(firstNameN).performTextInput(firstNameP)
        rule.onNode(
            hasText(firstNameP)
        ).assertIsDisplayed()
        rule.onNode(lastNameN).performTextInput(lastNameP)
        rule.onNode(
            hasText(lastNameP)
        ).assertIsDisplayed()
        rule.onNode(phoneNumberN).performTextInput(phoneNumberP)
        rule.onNode(
            hasText(phoneNumberP)
        ).assertIsDisplayed()
        rule.onNode(secondaryPhoneNumberN).performTextInput(secondaryPhoneNumberP)
        rule.onNode(
            hasText(secondaryPhoneNumberP)
        ).assertIsDisplayed()
        rule.onNode(saveN).assertIsDisplayed()
        rule.onNode(
            saveN
                    and hasClickAction()
        ).assertIsEnabled()
    }

    @Test
    fun testNegativeEntries(){
        rule.setContent { AddNewShopScreen() }
        rule.onNode(shopnameN).performTextInput(shopname)
            rule.onNode(
            hasText(shopname)
        ).assertIsError()
        rule.onNode(balanceN).performTextInput(balance)
        rule.onNode(
            hasText(balance)
        ).assertIsDisplayed()
        rule.onNode(firstNameN).performTextInput(firstName)
        rule.onNode(
            hasText(firstName)
        ).assertIsDisplayed()
        rule.onNode(lastNameN).performTextInput(lastName)
        rule.onNode(
            hasText(lastName)
        ).assertIsDisplayed()
        rule.onNode(phoneNumberN).performTextInput(phoneNumber)
        rule.onNode(
            hasText(phoneNumber)
        ).assertIsDisplayed()
        rule.onNode(secondaryPhoneNumberN).performTextInput(secondaryPhoneNumber)
        rule.onNode(
            hasText(secondaryPhoneNumber)
        ).assertIsDisplayed()
        rule.onNode(saveN).assertIsDisplayed()
        rule.onNode(
            saveN
                    and hasClickAction()
                    and isEnabled()
        )
    }

}