package com.collection.kubera.ui.addnewshop

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.BALANCE_COLLECTION
import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.data.TodaysCollections
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
        Timber.i("saveShopDetails")
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
            Timber.i(prm.toString())
            firestore.collection(SHOP_COLLECTION)
                .add(prm).addOnSuccessListener {
                    prm.apply { id = it.id }
                    insertCollectionHistory(
                        prm
                    )
                    prm.balance?.let { it1 -> updateTotalBalance(it1) }
                    balance?.let { it1 -> updateTodaysCollection(it1.toLong()) }
                    val message = "New Shop Added Successfully"
                    Timber.i(message)
                    _uiState.value = AddNewShopUiState.AddNewShopSuccess(message)
                }.addOnFailureListener {
                    val message = "Shop is not added,please try again"
                    Timber.i(message)
                    _uiState.value =
                        AddNewShopUiState.AddNewShopError(message)
                    _uiState.value =
                        AddNewShopUiState.AddNewShopCompleted(message)
                }
        }

    }

    private fun insertCollectionHistory(shop: Shop) {
        Timber.tag("insertCollectionHistory").i("insertCollectionHistory")
        viewModelScope.launch(Dispatchers.IO) {
            val prm = CollectionModel().apply {
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
            Timber.i(prm.toString())
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .add(prm).addOnSuccessListener {
                    Timber.tag("Success").i("Collection history updated")
                }.addOnFailureListener {
                    val message = "Collection history not updated"
                    Timber.e(message)
                    _uiState.value =
                        AddNewShopUiState.AddNewShopError(message)
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
                            Timber.i("balance -> $balance")
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

    private fun updateTodaysCollection(b: Long) {
        Timber.tag("updateTodaysCollection").i("updateTodaysCollection")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(TODAYS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull { item ->
                        item.toObject(TodaysCollections::class.java)
                            ?.apply {
                                id = item.id
                            }
                    }.also { list ->
                        if (list.isNotEmpty()) {
                            Timber.tag("updateTodaysCollection").i("Update")
                            val prm = mutableMapOf<String, Any>()
                            val balance =  (list[0].balance) + (b)
                            val credit =  (list[0].credit) + (b)
                            prm["balance"] = balance
                            prm["credit"] = credit
                            list[0].id?.let { it1 ->
                                Timber.tag("updateTodaysCollection").i(it1)
                                Timber.tag("updateTodaysCollection").i("prm -> $prm")
                                firestore.collection(TODAYS_COLLECTION)
                                    .document(it1)
                                    .update(prm)
                                    .addOnSuccessListener {
                                        Timber.tag("updateTodaysCollection").i("Success")
                                    }
                                    .addOnFailureListener {
                                        Timber.tag("updateTodaysCollection")
                                            .e("Error deleting collection: $it")
                                    }
                            }
                        } else {
                            insertTodaysCollection(b)
                        }
                    }

                }
                .addOnFailureListener {
                    Timber.tag("updateTodaysCollection").e("$it")
                }
        }
    }

    private fun insertTodaysCollection(b: Long) {
        Timber.tag("insertTodaysCollection").i("Add new Entry")
        val prm = TodaysCollections()
        prm.balance = b
        prm.credit = b

        viewModelScope.launch(Dispatchers.IO) {
            Timber.i(prm.toString())
            firestore.collection(TODAYS_COLLECTION)
                .add(prm)
                .addOnSuccessListener {
                    Timber.tag("insertTodaysCollection").i("Success")
                }
                .addOnFailureListener { e ->
                    Timber.tag("insertTodaysCollection").e(e)
                }
        }
    }

    fun setPreference(pref: SharedPreferences) {
        this.pref = pref
    }
}