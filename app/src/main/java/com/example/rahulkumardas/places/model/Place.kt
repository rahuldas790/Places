package com.example.rahulkumardas.places.model

import android.os.Parcel
import android.os.Parcelable

class Place(val id: String, val name: String, val icon: String,
            val addr: String, val rating: Float, val photos: Array<String>,
            val types: Array<String>) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.createStringArray(),
            parcel.createStringArray()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(icon)
        parcel.writeString(addr)
        parcel.writeFloat(rating)
        parcel.writeStringArray(photos)
        parcel.writeStringArray(types)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }

}