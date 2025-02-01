package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("display_name") var displayName: String,
    @SerializedName("account_id") val accountID: String?,
    @SerializedName("profile_icon") val profileIcon: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("friend_status") var friendStatus: String?,
    @SerializedName("is_me") val isMe: Boolean?,
    @SerializedName("about_me") val aboutMe: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )
    {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(displayName)
        parcel.writeString(accountID)
        parcel.writeString(profileIcon)
        parcel.writeString(icon)
        parcel.writeString(friendStatus)
        if (isMe != null) parcel.writeByte(if (isMe!!) 1 else 0)
        if (aboutMe != null) parcel.writeString(aboutMe)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserProfile> {
        const val STATUS_FRIEND = "FRIEND"
        const val STATUS_REQUEST_SENT = "REQUEST_SENT"
        const val STATUS_ACCEPT_REQUEST = "ACCEPT_REQUEST"
        override fun createFromParcel(parcel: Parcel): UserProfile {
            return UserProfile(parcel)
        }

        override fun newArray(size: Int): Array<UserProfile?> {
            return arrayOfNulls(size)
        }
    }
}