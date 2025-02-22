package com.collection.kubera.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class CollectionHistory(
    var id: String = "",
    var shopId: String = "",
    var shopName: String = "",
    var s_shopName: String = "",
    var firstName: String = "",
    var s_firstName: String = "",
    var lastName: String = "",
    var s_lastName: String = "",
    var phoneNumber: String? = null,
    var secondPhoneNumber: String? = null,
    var mailId: String = "",
    var amount: Long? = null,
    var collectedBy : String = "Admin",
    var collectedById : String? = null,
    var transactionType: String? = null,
    var timestamp: Timestamp? = null,
): Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        null,
        "Admin",
        null,
        null,
        Timestamp.now())

    val datedmy: String
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            return formatter.format(date)
        }
    val time: String
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return formatter.format(date)
        }

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString(),
        parcel.readString()?:"",
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readParcelable(Timestamp::class.java.classLoader)
    ) {
    }

    companion object CREATOR : Parcelable.Creator<CollectionHistory> {
        override fun createFromParcel(parcel: Parcel): CollectionHistory {
            return CollectionHistory(parcel)
        }

        override fun newArray(size: Int): Array<CollectionHistory?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}
