package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/*
{
                "id": "5d2764f438407225f2463d3e",
                "message": "Sam",
                "type": "MESSAGE",
                "user": {
                    "id": "5ccd3725c0a67504084ab875",
                    "display_name": "Too Long Text Sam",
                    "account_id": "sam_yeung",
                    "icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/icon_1562252486545.jpg",
                    "profile_icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/profile_icon_1562865997743.jpg",
                    "about_me": "Hello world"
                },
                "post_date": 1562862836357,
                "is_edited": false
                "is_me":false
            }
 */
data class Comment(
    @SerializedName("id") val id: String,
    @SerializedName("message") var message: String,
    @SerializedName("user") val user: UserProfile,
    @SerializedName("type") val type: String,
    @SerializedName("post_date") val postDate: Long,
    @SerializedName("is_edited") val is_edited: Boolean,
    @SerializedName("is_me") val is_me: Boolean
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(UserProfile::class.java.classLoader),
        parcel.readString(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(message)
        parcel.writeParcelable(user, flags)
        parcel.writeString(type)
        parcel.writeLong(postDate)
        parcel.writeByte(if (is_edited) 1 else 0)
        parcel.writeByte(if (is_me) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}