package com.collection.kubera.ui.addnewshop

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.BALANCE_COLLECTION
import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.states.AddNewShopUiState
import com.collection.kubera.utils.USER_ID
import com.collection.kubera.utils.USER_NAME
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
    var pref: SharedPreferences? = null

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
                ) this.secondPhoneNumber = secondPhoneNumber
                if ((mailId ?: "").isNotEmpty()) this.mailId = mailId!!
                this.timestamp = Timestamp.now()
                this.status = true
            }
            firestore.collection(SHOP_COLLECTION)
                .add(prm).addOnSuccessListener {
                    prm.apply { id = it.id }
                    insertCollectionHistory(
                        prm
                    )
                    prm.balance?.let { it1 -> updateTotalBalance(it1) }
                    _uiState.value =
                        AddNewShopUiState.AddNewShopSuccess("New Shop Added Successfully")
                }.addOnFailureListener {
                    _uiState.value =
                        AddNewShopUiState.AddNewShopError("Shop is not added,please try again")
                    _uiState.value =
                        AddNewShopUiState.AddNewShopCompleted("Shop is not added,please try again")
                }
        }

    }

    private fun insertCollectionHistory(shop: Shop) {
        viewModelScope.launch(Dispatchers.IO) {
            val prm = CollectionHistory().apply {
                if (shop.id.isNotEmpty()) {
                    this.shopId = shop.id
                }
                if (shop.shopName.isNotEmpty()) this.shopName = shop.shopName
                if (shop.shopName.isNotEmpty()) this.s_shopName = shop.shopName.lowercase()
                if ((shop.balance ?: 0) > 1) this.amount = shop.balance
                if (shop.firstName.isNotEmpty()) this.firstName = shop.firstName
                if (shop.firstName.isNotEmpty()) this.s_firstName = shop.firstName.lowercase()
                if ((shop.lastName ?: "").isNotEmpty()) this.lastName = shop.lastName
                if ((shop.lastName ?: "").isNotEmpty()) this.s_lastName =
                    (shop.lastName ?: "").lowercase()
                if (shop.phoneNumber.toString().isNotEmpty()) this.phoneNumber = shop.phoneNumber
                if (
                    shop.secondPhoneNumber != null &&
                    (secondPhoneNumber ?: "").isNotEmpty()
                ) {
                    this.secondPhoneNumber = secondPhoneNumber!!
                }
                if ((shop.mailId ?: "").isNotEmpty()) this.mailId = shop.mailId
                this.collectedById = pref?.getString(USER_ID, null)
                this.collectedBy = pref?.getString(USER_NAME, null) ?: "Admin"
                this.timestamp = Timestamp.now()
                this.transactionType = "Credit"
            }
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .add(prm).addOnSuccessListener {
                    Timber.tag("Success").i("Collection history updated")
                }.addOnFailureListener {
                    _uiState.value =
                        AddNewShopUiState.AddNewShopError("Collection history not updated")
                }
        }
    }


    private fun updateTotalBalance(b: Long) {
        Timber.tag("updateTotalBalance").i("updateTotalBalance")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(BALANCE_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(BalanceAmount::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }.also {
                        if (it.isNotEmpty()) {
                            val balance = (it[0].balance) + b
                            firestore.collection(BALANCE_COLLECTION)
                                .document(it[0].id!!)
                                .update(mapOf("balance" to balance))
                                .addOnSuccessListener {
                                    Timber.tag("updateTotalBalance").i("Success")
                                }
                                .addOnFailureListener {
                                    Timber.tag("updateTotalBalance")
                                        .e("Error deleting collection: $it")
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.tag("getBalance").e("$it")
                }
        }
    }

    fun setPreference(pref: SharedPreferences) {
        this.pref = pref
    }
}