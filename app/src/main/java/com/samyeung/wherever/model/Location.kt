package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import android.location.Location.FORMAT_SECONDS



class Location(var lat:Double = 0.0,var lng:Double = 0.0):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "Long:${this.lng}, Lat: ${this.lat}"
    }
    fun toLocation():android.location.Location{
        val location = android.location.Location(convert(this.lat,this.lng))
        return location
    }
    fun deepCopy():Location{
        return Location(this.lat,this.lng)
    }

    private fun convert(latitude: Double, longitude: Double): String {
        val builder = StringBuilder()


        val latitudeDegrees = android.location.Location.convert(Math.abs(latitude), android.location.Location.FORMAT_SECONDS)
        val latitudeSplit = latitudeDegrees.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        builder.append(latitudeSplit[0])
        builder.append("°")
        builder.append(latitudeSplit[1])
        builder.append("'")
        builder.append(latitudeSplit[2])
        builder.append("\"")
        if (latitude < 0) {
            builder.append(" S")
        } else {
            builder.append(" N")
        }
        builder.append("\n")


        val longitudeDegrees = android.location.Location.convert(Math.abs(longitude), android.location.Location.FORMAT_SECONDS)
        val longitudeSplit = longitudeDegrees.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        builder.append(longitudeSplit[0])
        builder.append("°")
        builder.append(longitudeSplit[1])
        builder.append("'")
        builder.append(longitudeSplit[2])
        builder.append("\"")
        if (longitude < 0) {
            builder.append(" W")
        } else {
            builder.append(" E")
        }
        return builder.toString()
    }

}