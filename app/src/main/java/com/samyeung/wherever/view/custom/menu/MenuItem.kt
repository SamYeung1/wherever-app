package com.samyeung.wherever.view.custom.menu

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes

class MenuItem(
    val title: String,
    val description: String, @DrawableRes val icon: Int, @ColorRes val color: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(icon)
        this.color?.let {
            parcel.writeInt(it)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuItem> {
        override fun createFromParcel(parcel: Parcel): MenuItem {
            return MenuItem(parcel)
        }

        override fun newArray(size: Int): Array<MenuItem?> {
            return arrayOfNulls(size)
        }
    }
}