package com.collection.kubera.ui.addnewshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.Shop
import com.collection.kubera.data.User
import com.collection.kubera.states.AddNewShopUiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AddNewShopViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<AddNewShopUiState> =
        MutableStateFlow(AddNewShopUiState.Initial)
    val uiState: StateFlow<AddNewShopUiState> =
        _uiState.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users
    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers() {
        Timber.v("getUsers")
        _uiState.value = AddNewShopUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val snapshot = firestore.collection("user")
                .orderBy("username", Query.Direction.ASCENDING)
                .get().await()
            _users.value = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)?.apply { id = it.id }
            }
            _uiState.value = AddNewShopUiState.AddNewShopSuccess("Success")
            _uiState.value = AddNewShopUiState.Initial
        }
    }

    fun login(userName: String, password: String) {
        Timber.v("login")
        _uiState.value = AddNewShopUiState.Loading
        users.value.indexOfFirst { (it.username.equals(userName) && it.password.equals(password)) }
            .also { result ->
                result.takeIf { result >= 0 }?.let {
                    _uiState.value =
                        AddNewShopUiState.AddNewShopSuccess("Successfully logged in")
                } ?: run {
                    _uiState.value =
                        AddNewShopUiState.AddNewShopError("Please enter correct credentials")
                }
            }
    }

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
                if (location.isNotEmpty()) this.location = location
                if ((landmark?:"").isNotEmpty()) this.landmark = landmark!!
                if ((balance?:"").isNotEmpty()) this.balance = (balance?:"0").toLong()
                if (firstName.isNotEmpty()) this.firstName = firstName
                if ((lastName?:"").isNotEmpty()) this.lastName = lastName!!
                if (phoneNumber.toString().isNotEmpty()) this.phoneNumber = phoneNumber
                if (secondPhoneNumber !=null && secondPhoneNumber.toString().isNotEmpty()) this.secondPhoneNumber = secondPhoneNumber!!
                if ((mailId?:"").isNotEmpty()) this.mailId = mailId!!
                this.timestamp = Timestamp.now()
                this.status = true
            }
            firestore.collection("shop")
                .add(prm).addOnSuccessListener {
                    _uiState.value = AddNewShopUiState.AddNewShopSuccess("Success")
                }.addOnFailureListener {
                    _uiState.value = AddNewShopUiState.AddNewShopError("Error")
                }
        }

    }
}