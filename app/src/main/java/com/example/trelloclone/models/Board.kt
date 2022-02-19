package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val Name: String = "",
    val Image: String = "",
    val CreatedBy: String = "",
    val AssignedTo: ArrayList<String> = ArrayList(),
    var DocumentId: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,

    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(p0: Parcel?, p1: Int) = with(p0) {
        this!!.writeString(Name)
        this.writeString(Image)
        this.writeString(CreatedBy)
        this.writeStringList(AssignedTo)
        this.writeString(DocumentId)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
