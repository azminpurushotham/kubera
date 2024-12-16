package com.collection.kubera.ui.addnewshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Shop
import com.collection.kubera.states.AddNewShopUiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AddNewShopViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<AddNewShopUiState> =
        MutableStateFlow(AddNewShopUiState.Initial)
    val uiState: StateFlow<AddNewShopUiState> =
        _uiState.asStateFlow()
    private val firestore = FirebaseFirestore.getInstance()

    fun saveShopDetails(
        shopName: String,
        location: String,
        landmark: String?,
        balance: String?,
        firstName: String,
        lastName: String?,
        phoneNumber: String,
        secondPhoneNumber: String?,
        mailId: String?
    ) {
        Timber.v("saveShopDetails")
        _uiState.value = AddNewShopUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val prm = Shop().apply {
                if (shopName.isNotEmpty()) this.shopName = shopName
                if (shopName.isNotEmpty()) this.s_shopName = shopName.lowercase()
                if (location.isNotEmpty()) this.location = location
                if ((landmark ?: "").isNotEmpty()) this.landmark = landmark!!
                if ((balance ?: "").isNotEmpty()) this.balance = (balance ?: "0").toLong()
                if (firstName.isNotEmpty()) this.firstName = firstName
                if (firstName.isNotEmpty()) this.s_firstName = firstName.lowercase()
                if ((lastName ?: "").isNotEmpty()) this.lastName = lastName!!
                if ((lastName ?: "").isNotEmpty()) this.s_lastName = (lastName ?: "").lowercase()
                if (phoneNumber.toString().isNotEmpty()) this.phoneNumber = phoneNumber
                if (secondPhoneNumber != null && secondPhoneNumber.toString()
                        .isNotEmpty()
                ) this.secondPhoneNumber = secondPhoneNumber!!
                if ((mailId ?: "").isNotEmpty()) this.mailId = mailId!!
                this.timestamp = Timestamp.now()
                this.status = true
            }
            firestore.collection("shop")
                .add(prm).addOnSuccessListener {
                    _uiState.value =
                        AddNewShopUiState.AddNewShopSuccess("New Shop Added Successfully")
                }.addOnFailureListener {
                    _uiState.value =
                        AddNewShopUiState.AddNewShopError("Shop is not added,please try again")
                    _uiState.value = AddNewShopUiState.AddNewShopCompleted("Shop is not added,please try again")
                }
        }

    }
}