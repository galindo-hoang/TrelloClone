package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    var name:String = "",
    var createBy:String = "",
    var assignedTo: ArrayList<String> = arrayListOf(),
    var selectedColor: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(createBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(selectedColor)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
