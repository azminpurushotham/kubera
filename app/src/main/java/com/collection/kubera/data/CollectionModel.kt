package com.collection.kubera.data

import android.os.Parcel
import android.os.Parcelable
import com.collection.kubera.utils.dmyh
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class CollectionModel(
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("Id")
    var id: String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("ShopId")
    var shopId: String? = null,
    @field:JsonProperty("ShopName")
    var shopName:String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("ShopNameL")
    var s_shopName:String? = null,
    @field:JsonProperty("FirstName")
    var firstName:String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("FirstNameL")
    var s_firstName:String? = null,
    @field:JsonProperty("LastName")
    var lastName:String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("LastNameL")
    var s_lastName:String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("PhoneNumber")
    var phoneNumber: String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("SecondaryPhoneNumber")
    var secondPhoneNumber: String? = null,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("MailId")
    var mailId:String? = null,
    @field:JsonProperty("Amount")
    var amount: Long? = null,
    @field:JsonProperty("CollectedBy")
    var collectedBy : String = "Admin",
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
    @field:JsonProperty("CollectedById")
    var collectedById : String? = null,
    @field:JsonProperty("TransactionType")
    var transactionType: String? = null,
//    @field:JsonIgnore
//    var stability: Int? = 1,
    @field:JsonIgnore // Ignore this property during JSON/CSV processing issue with Timestamp parsing
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
//        null,
        Timestamp.now(),
        )

    @field:JsonIgnore // Ignore this property during JSON/CSV processing
    var datedmy: String= ""
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            return formatter.format(date)
        }

    @field:JsonIgnore // Ignore this property during JSON/CSV processing
    var time: String = ""
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            return formatter.format(date)
        }

    @field:JsonProperty("Time")
    var datetime : String = ""
        get() {
            val date = timestamp?.toDate()
            val formatter = SimpleDateFormat(dmyh, Locale.getDefault())
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
//        parcel.readInt(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
    )

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
//        stability?.let { parcel.writeInt(it) }
        parcel.writeParcelable(timestamp, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollectionModel> {
        override fun createFromParcel(parcel: Parcel): CollectionModel {
            return CollectionModel(parcel)
        }

        override fun newArray(size: Int): Array<CollectionModel?> {
            return arrayOfNulls(size)
        }
    }
}
