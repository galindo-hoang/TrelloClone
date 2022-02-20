package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName

data class User(
    @set: PropertyName("id")
    var Id: String = "",
    @set: PropertyName("name")
    var Name: String = "",
    @set: PropertyName("email")
    var Email: String = "",
    @set: PropertyName("image")
    var Image: String = "",
    @set: PropertyName("mobile")
    var Mobile: Long = 0,
    @set: PropertyName("fcmToken")
    var fcmToken: String = ""
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
