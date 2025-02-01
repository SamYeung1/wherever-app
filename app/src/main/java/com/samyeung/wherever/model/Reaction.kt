package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/*
 "user": {
                    "id": "5ccd370451d6bd0402d3cb0a",
                    "display_name": "SamYeung",
                    "account_id": null,
                    "icon": null,
                    "profile_icon": null
                },
                "type": "LOVE",
                "post_date": "2019-06-20T14:32:14.458Z"
 */
data class Reaction(
    @SerializedName("user") val user: UserProfile,
    @SerializedName("type") val type: String,
    @SerializedName("post_date") val postDate: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(UserProfile::class.java.classLoader),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeString(type)
        parcel.writeLong(postDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reaction> {
        const val TYPE_LOVE = "LOVE"
        override fun createFromParcel(parcel: Parcel): Reaction {
            return Reaction(parcel)
        }

        override fun newArray(size: Int): Array<Reaction?> {
            return arrayOfNulls(size)
        }
    }
}