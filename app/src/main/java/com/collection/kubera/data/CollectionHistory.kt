package com.collection.kubera.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class CollectionHistory(
    var id: String? = null,
    var shopId: String? = null,
    var shopName:String? = null,
    var s_shopName:String? = null,
    var firstName:String? = null,
    var s_firstName:String? = null,
    var lastName:String? = null,
    var s_lastName:String? = null,
    var phoneNumber: String? = null,
    var secondPhoneNumber: String? = null,
    var mailId:String? = null,
    var amount: Long? = null,
    var collectedBy : String = "Admin",
    var collectedById : String? = null,
    var transactionType: String? = null,
    var timestamp: Timestamp? = null,
): Parcelable {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()?:"Admin",
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(shopId)
        parcel.writeString(shopName)
        parcel.writeString(s_shopName)
        parcel.writeString(firstName)
        parcel.writeString(s_firstName)
        parcel.writeString(lastName)
        parcel.writeString(s_lastName)
        parcel.writeString(phoneNumber)
        parcel.writeString(secondPhoneNumber)
        parcel.writeString(mailId)
        parcel.writeValue(amount)
        parcel.writeString(collectedBy)
        parcel.writeString(collectedById)
        parcel.writeString(transactionType)
        parcel.writeParcelable(timestamp, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollectionHistory> {
        override fun createFromParcel(parcel: Parcel): CollectionHistory {
            return CollectionHistory(parcel)
        }

        override fun newArray(size: Int): Array<CollectionHistory?> {
            return arrayOfNulls(size)
        }
    }


}
