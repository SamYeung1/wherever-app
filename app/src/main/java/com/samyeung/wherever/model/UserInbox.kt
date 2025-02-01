package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/*
"id": "5d1b5fbf8657e125ed75045f",
        "icon": null,
        "inbox_title": "Too Long Text Sam liked image",
        "inbox_content": "Too Long Text Sam liked image",
        "link": "wherever://image_reaction?trace_id=5d17a10b2d414277b217a73c",
        "user": {
            "id": "5ccd3725c0a67504084ab875",
            "display_name": "Too Long Text Sam",
            "account_id": "sam_yeung",
            "icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/icon_1562252486545.jpg",
            "profile_icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/profile_icon_1562865997743.jpg",
            "about_me": "Hello world"
        },
        "trace": {
            "id": "5d17a10b2d414277b217a73c",
            "banners": [
                "https://whereverdev.s3.amazonaws.com/collection/5ccd3725c0a67504084ab875/image_1561829642432.jpg"
            ],
            "thumbnail": "https://whereverdev.s3.amazonaws.com/collection/5ccd3725c0a67504084ab875/image_1561829642432_thumbnail.jpg",
            "title": "Test",
            "description": "",
            "longitude": 113.975,
            "latitude": 22.3964238,
            "tags": [],
            "user": {
                "id": "5ccd3725c0a67504084ab875",
                "display_name": "Too Long Text Sam",
                "account_id": "sam_yeung",
                "icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/icon_1562252486545.jpg",
                "profile_icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/profile_icon_1562865997743.jpg",
                "about_me": "Hello world"
            },
            "post_date": 1561829643097
        },
        "comment": null,
        "type": "TRACE",
        "post_date": 1561829643097
 */
data class UserInbox(
    @SerializedName("id") val id: String,
    @SerializedName("icon") val icon: String?,
    @SerializedName("inbox_title") val inboxTitle: String,
    @SerializedName("inbox_content") val inboxContent: String,
    @SerializedName("link") val link: String,
    @SerializedName("user") val user: UserProfile?,
    @SerializedName("trace") val trace: Trace?,
//    @SerializedName("comment") val comment:Any?,
    @SerializedName("type") val type: String,
    @SerializedName("post_date") val postDate: Long,
    @SerializedName("is_read") val isRead: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        if (parcel.readParcelable<UserProfile>(UserProfile::class.java.classLoader) != null) parcel.readParcelable(
            UserProfile::class.java.classLoader
        ) else null,
        if (parcel.readParcelable<Trace>(Trace::class.java.classLoader) != null) parcel.readParcelable(Trace::class.java.classLoader) else null,
//        TODO if (parcel.readParcelable<Comment>(Comment::class.java.classLoader) != null) parcel.readParcelable(Comment::class.java.classLoader) else null,
        parcel.readString(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(icon)
        parcel.writeString(inboxTitle)
        parcel.writeString(inboxContent)
        parcel.writeString(link)
        if (user != null) parcel.writeParcelable(user, flags)
        if (trace != null) parcel.writeParcelable(trace, flags)
        parcel.writeString(type)
        parcel.writeLong(postDate)
        parcel.writeByte(if (isRead) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInbox> {
        const val TRACE = "TRACE"
        const val COMMENT = "COMMENT"
        override fun createFromParcel(parcel: Parcel): UserInbox {
            return UserInbox(parcel)
        }

        override fun newArray(size: Int): Array<UserInbox?> {
            return arrayOfNulls(size)
        }
    }
}