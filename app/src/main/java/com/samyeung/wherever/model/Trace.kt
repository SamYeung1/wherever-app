package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/*
"id": "5cf276de98565f15a098d8cb",
        "banners": [
            "https://whereverdev.s3.amazonaws.com/https://whereverdev.s3.amazonaws.com/collection/5ccd370451d6bd0402d3cb0a/image_1559394001461.jpg"
        ],
        "thumbnail": "https://whereverdev.s3.amazonaws.com/https://whereverdev.s3.amazonaws.com/collection/5ccd370451d6bd0402d3cb0a/image_1559394001461_thumbnail.jpg",
        "title": "Test",
        "description": "",
        "longitude": 113.975976,
        "latitude": 22.404624,
        "tags": [
            "A",
            "B"
        ],
        "user": {
            "id": "5ccd370451d6bd0402d3cb0a",
            "display_name": "SamYeung",
            "account_id": null,
            "icon": null,
            "profile_icon": null
        },
        "post_date": "2019-06-01T13:00:14.621Z",
        "comment_total": 1,
        "reaction_total": 1,
        "distance": 0
 */
data class Trace(
    @SerializedName("id") val id: String,
    @SerializedName("banners") val banners: Array<String>,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("tags") val tags: Array<String>,
    @SerializedName("user") val user: UserProfile,
    @SerializedName("post_date") val postDate: Long,
    @SerializedName("comment_total") var commentTotal: Long?,
    @SerializedName("reaction_total") var reactionTotal: Long?,
    @SerializedName("distance") val distance: Double?,
    @SerializedName("is_reacted") var isReacted: Boolean?,
    @SerializedName("token_date") val tokenDate: Long,
    @SerializedName("is_favourite") var isFavourite: Boolean?,
    @SerializedName("is_me") var isMe:Boolean = false

) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArray(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.createStringArray(),
        parcel.readParcelable(UserProfile::class.java.classLoader),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeStringArray(banners)
        parcel.writeString(thumbnail)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeStringArray(tags)
        parcel.writeParcelable(user, flags)
        parcel.writeLong(postDate)
        if(commentTotal !=null) parcel.writeLong(commentTotal!!) else parcel.writeLong(0)
        if(reactionTotal !=null) parcel.writeLong(reactionTotal!!) else parcel.writeLong(0)
        if(distance !=null) parcel.writeDouble(distance)
        if(isReacted !=null) parcel.writeByte(if (isReacted!!) 1 else 0) else parcel.writeByte(0)
        parcel.writeLong(tokenDate)
        if(isFavourite !=null) parcel.writeByte(if (isReacted!!) 1 else 0) else parcel.writeByte(0)
        parcel.writeByte(if (isMe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trace> {
        override fun createFromParcel(parcel: Parcel): Trace {
            return Trace(parcel)
        }

        override fun newArray(size: Int): Array<Trace?> {
            return arrayOfNulls(size)
        }
    }
}