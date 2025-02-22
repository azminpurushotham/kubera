package com.collection.kubera.ui.shopdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.BALANCE_COLLECTION
import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TODAYS_COLLECTION
import com.collection.kubera.data.TodaysCollections
import com.collection.kubera.states.ShopDetailUiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

class ShopDetailsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ShopDetailUiState> =
        MutableStateFlow(ShopDetailUiState.Initial)
    val uiState: Flow<ShopDetailUiState> =
        _uiState.distinctUntilChanged { old, new -> old == new } // Compare only `data` // Ensures only distinct values are emitted
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop
    private val firestore = FirebaseFirestore.getInstance()
    var c = 0

    private fun updateState(newState: ShopDetailUiState) {
        if (_uiState.value != newState) {
            _uiState.value = newState
        }
    }

    fun setShop(model: Shop){
        _shop.value = model
    }

    fun getShopDetails(
        id: String
    ) {
        Timber.v("getShopDetails")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(SHOP_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data?.isNotEmpty() == true) {
                        _shop.value = result.toObject(Shop::class.java)
                            ?.apply {
                                this.id = id
                            }
                    }
                    updateState(ShopDetailUiState.ShopDetailSuccess("Success"))
                }.addOnFailureListener {
                    updateState(ShopDetailUiState.ShopDetailError("Error"))
                }
        }

    }

    fun updateBalance(id: String?, b: String, selectedOption: String) {
        id?.let {
            _uiState.value = ShopDetailUiState.Loading
            val balance = if (selectedOption == "Credit") {
              (shop.value?.balance?:0L) + (b.toLong())
            } else {
                (shop.value?.balance?:0L) - (b.toLong())
            }
            firestore.collection(SHOP_COLLECTION)
                .document(it)
                .update("balance", balance)
                .addOnSuccessListener {
                    insertCollectionHistory(
                        id,
                        b = b,
                        selectedOption = selectedOption
                    )
                    updateBalanceCollections(balance,selectedOption)
                    updateTodaysCollection(balance,selectedOption)
                    updateState(ShopDetailUiState.ShopDetailToast("Successfully balance updated"))
                    updateState(ShopDetailUiState.ShopDetailsPopBack("Successfully balance updated"))
                }.addOnFailureListener {
                    updateState(ShopDetailUiState.ShopDetailToast("Balance not updated"))
                }
        }
    }

    private fun insertCollectionHistory(id: String?, b: String, selectedOption: String) {
        id?.let {
            val balance = if (selectedOption == "Credit") {
                b
            } else {
                "-${b}"
            }
            viewModelScope.launch(Dispatchers.IO) {
                val shop = shop.value
                val prm = CollectionHistory().apply {
                    if (shop?.id?.isEmpty() != true) {
                        this.shopId = shop?.id!!
                    }
                    if (shop.shopName.isEmpty() != true) this.shopName = shop.shopName
                    if (shop.shopName.isEmpty() != true) this.s_shopName = shop.shopName.lowercase()
                    if ((balance ?: "").isEmpty() != true) this.amount = (balance ?: "0").toLong()
                    if (shop.firstName.isEmpty() != true) this.firstName = shop.firstName
                    if (shop.firstName.isEmpty() != true) this.s_firstName = shop.firstName.lowercase()
                    if ((shop.lastName ?: "").isNotEmpty()) this.lastName = shop.lastName
                    if ((shop.lastName ?: "").isNotEmpty()) this.s_lastName = (shop.lastName ?: "").lowercase()
                    if (shop.phoneNumber.toString().isNotEmpty()) this.phoneNumber = shop.phoneNumber
                    if (shop.secondPhoneNumber != null && secondPhoneNumber.toString()
                            .isNotEmpty()
                    ) this.secondPhoneNumber = secondPhoneNumber!!
                    if ((shop.mailId ?: "").isNotEmpty()) this.mailId = shop.mailId
                    this.timestamp = Timestamp.now()
                    this.transactionType = selectedOption
                }
                firestore.collection("collection_history")
                    .add(prm).addOnSuccessListener {
                        updateState(ShopDetailUiState.ShopDetailToast("Successfully collection history updated"))
                    }.addOnFailureListener {
                        updateState(ShopDetailUiState.ShopDetailToast("Collection history not updated"))
                    }
            }
        }
    }

    private fun updateBalanceCollections(b: Long, selectedOption: String) {
        Timber.tag("updateBalanceCollections").i("updateBalanceCollections")
        viewModelScope.launch (Dispatchers.IO){
            firestore.collection(BALANCE_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(BalanceAmount::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }.also {
                        if(it.isNotEmpty()){
                            val balance = if (selectedOption == "Credit") {
                                (it[0].balance) + (b )
                            } else {
                                (it[0].balance) - (b )
                            }
                            firestore.collection(BALANCE_COLLECTION)
                                .document(it[0].id!!)
                                .update(mapOf("balance" to balance))
                                .addOnSuccessListener {
                                    Timber.tag("updateBalanceCollections").i("Success")
                                }
                                .addOnFailureListener {
                                    Timber.tag("updateBalanceCollections").e("Error deleting collection: $it")
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.tag("getBalance").e("$it")
                }
        }
    }

    private fun updateTodaysCollection(b: Long, selectedOption: String) {
        Timber.tag("updateTodaysCollection").i("updateTodaysCollection")
        viewModelScope.launch (Dispatchers.IO){
            firestore.collection(TODAYS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.mapNotNull {
                        it.toObject(TodaysCollections::class.java)
                            ?.apply {
                                id = it.id
                            }
                    }.also {
                        if(it.isNotEmpty()){
                            val prm = mutableMapOf<String, Any>()
                            val balance = if (selectedOption == "Credit") {
                                (it[0].balance) + (b )
                            } else {
                                (it[0].balance) - (b )
                            }
                            prm["balance"] = balance
                             if (selectedOption == "Credit") {
                                 prm["credit"] = (it[0].credit) + b
                             }
                            if (selectedOption == "Debit") {
                                prm["debit"] = (it[0].debit) - b
                            }
                            firestore.collection(BALANCE_COLLECTION)
                                .document(it[0].id)
                                .update(prm)
                                .addOnSuccessListener {
                                    Timber.tag("updateTodaysCollection").i("Success")
                                }
                                .addOnFailureListener {
                                    Timber.tag("updateTodaysCollection").e("Error deleting collection: $it")
                                }
                        }else{
                            val prm = TodaysCollections()
                            val balance = if (selectedOption == "Credit") {
                                (it[0].balance) + (b)
                            } else {
                                (it[0].balance) - (b)
                            }
                            prm.balance = balance
                            if (selectedOption == "Credit") {
                                prm.credit = (it[0].credit) + b
                            }
                            if (selectedOption == "Debit") {
                                prm.debit = (it[0].debit) - b
                            }

                            firestore.collection(TODAYS_COLLECTION)
                                .add(prm)
                                .addOnSuccessListener {
                                    Timber.tag("updateTodaysCollection").i("Success")
                                }
                                .addOnFailureListener {e->
                                    Timber.tag("updateTodaysCollection").e(e)
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.tag("getBalance").e("$it")
                }
        }
    }
}