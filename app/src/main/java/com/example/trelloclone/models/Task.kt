package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName

data class Task(
//    @set: PropertyName("title")
    var Title: String = "",
//    @set: PropertyName("createBy")
    var createBy:String = "",
    var cardList: ArrayList<Card> = arrayListOf()
)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(p0: Parcel?, p1: Int) = with(p0) {
        this!!.writeString(Title)
        this.writeString(createBy)
        this.writeTypedList(cardList)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
