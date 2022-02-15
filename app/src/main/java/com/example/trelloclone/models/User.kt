package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val Id: String = "",
    val Name: String = "",
    val Email: String = "",
    val Image: String = "",
    val Mobile: Long = 0,
    val fcmToken: String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) = with(dest) {
        this!!.writeString(Id)
        this.writeString(Name)
        this.writeString(Email)
        this.writeString(Image)
        this.writeLong(Mobile)
        this.writeString(fcmToken)
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
