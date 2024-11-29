package com.collection.kubera.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class User(
    val loggedintime: Timestamp? = Timestamp.now(),
    val password: String = "",
    val status: Boolean = false,
    val username: String = ""
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString()?:"",
        parcel.readByte() != 0.toByte(),
        parcel.readString()?:""
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(loggedintime, flags)
        parcel.writeString(password)
        parcel.writeByte(if (status) 1 else 0)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}