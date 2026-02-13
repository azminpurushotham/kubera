package com.collection.kubera.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class BalanceAmount(
    val balance: Long = 0L,
    var id: String? = null,
    val timestamp: Timestamp = Timestamp.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader)!!
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<BalanceAmount> {
        override fun createFromParcel(parcel: Parcel): BalanceAmount {
            return BalanceAmount(parcel)
        }

        override fun newArray(size: Int): Array<BalanceAmount?> {
            return arrayOfNulls(size)
        }
    }
}