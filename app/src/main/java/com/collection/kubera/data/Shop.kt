package com.collection.kubera.data

import com.google.firebase.Timestamp

data class Shop(
    var id: String,
    var shopName: String,
    var location: String,
    var landmark: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: Long,
    var secondPhoneNumber: Long,
    var mailId: String,
    var date: Timestamp,
    var status: Boolean
){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        0.toLong(),
        0.toLong(),
        "",
        Timestamp.now(),
        false)
}
