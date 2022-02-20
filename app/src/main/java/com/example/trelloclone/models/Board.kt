package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName

data class Board(
    @set: PropertyName("name")
    var Name: String = "",
    @set: PropertyName("image")
    var Image: String = "",
    @set: PropertyName("createdBy")
    var CreatedBy: String = "",
    @set: PropertyName("assignedTo")
    var AssignedTo: ArrayList<String> = ArrayList(),
    @set: PropertyName("documentId")
    var DocumentId: String = "",
    @set: PropertyName("taskList")
    var taskList: ArrayList<Task> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!

    )
    override fun describeContents(): Int = 0

    override fun writeToParcel(p0: Parcel?, p1: Int) = with(p0) {
        this!!.writeString(Name)
        this.writeString(Image)
        this.writeString(CreatedBy)
        this.writeStringList(AssignedTo)
        this.writeString(DocumentId)
        this.writeTypedList(taskList)
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
