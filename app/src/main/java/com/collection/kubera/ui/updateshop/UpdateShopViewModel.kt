package com.collection.kubera.ui.updateshop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Shop
import com.collection.kubera.states.UpdateShopUiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class UpdateShopViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UpdateShopUiState> =
        MutableStateFlow(UpdateShopUiState.Initial)
    val uiState: StateFlow<UpdateShopUiState> =
        _uiState.asStateFlow()
    private val _shop = MutableStateFlow<Shop?>(null)
    val shop: StateFlow<Shop?> get() = _shop
    private val firestore = FirebaseFirestore.getInstance()

    private fun updateState(newState: UpdateShopUiState) {
        if (_uiState.value != newState) {
            _uiState.value = newState
        }
    }

    fun saveShopDetails(
        shopName: String?,
        location: String?,
        landmark: String?,
        balance: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        secondPhoneNumber: String?,
        mailId: String?
    ) {
        Timber.v("saveShopDetails")
        _uiState.value = UpdateShopUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val ud = mutableMapOf<String,Any>()
            if ((shopName ?: "").isNotEmpty()) {
                ud["shopName"] = shopName!!
                ud["s_shopName"] = shopName.lowercase()
            }
            if ((firstName ?: "").isNotEmpty()) {
                ud["firstName"] = firstName!!
                ud["s_firstName"] = firstName.lowercase()
            }
            if ((lastName ?: "").isNotEmpty()) {
                ud["lastName"] = lastName!!
                ud["s_lastName"] = lastName.lowercase()
            }
            if ((balance ?: "").isNotEmpty()) {
                ud["balance"] = balance!!.toLong()
            }
            if ((location ?: "").isNotEmpty()) {
                ud["location"] = location!!
            }
            if ((landmark ?: "").isNotEmpty()) {
                ud["landmark"] = landmark!!
            }
            if ((phoneNumber ?: "").isNotEmpty()) {
                ud["phoneNumber"] = phoneNumber!!
            }
            if ((secondPhoneNumber ?: "").isNotEmpty()) {
                ud["secondPhoneNumber"] = secondPhoneNumber!!
            }
            if ((mailId ?: "").isNotEmpty()) {
                ud["mailId"] = mailId!!
            }
            ud["timestamp"] = Timestamp.now()
            shop.value?.status?.let { ud.put("status", it) }

            Timber.tag("UPDATE").i(ud.toString())
            shop.value?.id?.let {
                firestore.collection("shop")
                    .document(it)
                    .update(ud).addOnSuccessListener {
                        _uiState.value =
                            UpdateShopUiState.UpdateShopSuccess("Shop details updated successfully")
                    }.addOnFailureListener {
                        _uiState.value =
                            UpdateShopUiState.UpdateShopError("Shop details is not updated,please try again")
                        _uiState.value =
                            UpdateShopUiState.UpdateShopCompleted("Shop details is not updated,please try again")
                    }
            }
        }

    }

    fun getShopDetails(
        id: String
    ) {
        Timber.v("getShopDetails")
        updateState(UpdateShopUiState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("shop")
                .document(id)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data?.isNotEmpty() == true) {
                        _shop.value = result.toObject(Shop::class.java)
                            ?.apply {
                                this.id = id
                            }
                    }
                    updateState(UpdateShopUiState.ShopDetailSuccess("Success"))
                }.addOnFailureListener {
                    updateState(UpdateShopUiState.ShopDetailError("Error"))
                }
        }

    }

    fun setShop(model: Shop) {
        _shop.value = model
    }
}