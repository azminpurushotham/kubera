package com.collection.kubera.data

import com.google.firebase.Timestamp

data class TodaysCollections(
    var balance: Long = 0,
    var credit: Long = 0,
    val datedmy: String?,
    var debit: Long = 0,
    var id: String,
    val timestamp: Timestamp = Timestamp.now()
){
    constructor():this(0,0,null,0,"",Timestamp.now())
}