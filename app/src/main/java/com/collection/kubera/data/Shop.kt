package com.collection.kubera.data

import android.os.Parcel
import android.os.Parcelable
import com.collection.kubera.utils.dmyh
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class Shop(
    @field:JsonProperty("Id")
    var id: String = "",
    @field:JsonProperty("ShopName")
    var shopName: String = "",
    @field:JsonProperty("ShopNameL")
    var s_shopName: String = "",
    @field:JsonProperty("Location")
    var location: String = "",
    @field:JsonProperty("Landmark")
    var landmark: String? = "",
    @field:JsonProperty("FirstName")
    var firstName: String = "",
    @field:JsonProperty("FirstNameL")
    var s_firstName: String = "",
    @field:JsonProperty("LastName")
    var lastName: String = "",
    @field:JsonProperty("LastNameL")
    var s_lastName: String = "",
    @field:JsonProperty("PhoneNumber")
    var phoneNumber: String? = null,
    @field:JsonProperty("SecondaryPhoneNumber")
    var secondPhoneNumber: String? = null,
    @field:JsonProperty("MailId")
    var mailId: String = "",
    @field:JsonProperty("Balance")
    var balance: Long? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    var timestamp: Timestamp? = null,
    @field:JsonProperty("Status")
    var status: Boolean = true,
) : Parcelable {
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
        "",
        null,
        Timestamp.now(),
        false,
    )

    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    var datedmy: String = ""
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            return formatter.format(date)
        }

    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    var time: String = ""
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return formatter.format(date)
        }

    @field:JsonProperty("Time")
    var datetime: String = ""
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat(dmyh, Locale.getDefault())
            return formatter.format(date)
        }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readByte() != 0.toByte(),
    )

    companion object CREATOR : Parcelable.Creator<Shop> {
        override fun createFromParcel(parcel: Parcel): Shop {
            return Shop(parcel)
        }

        override fun newArray(size: Int): Array<Shop?> {
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
