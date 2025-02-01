package com.samyeung.wherever.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName

data class Announcement(
    @SerializedName("id") val id: String,
    @SerializedName("banner") val banner: String?,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("post_date") val postDate: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        if (parcel.readString() != null) parcel.readString() else null,
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(banner)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeLong(postDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Announcement> {
        override fun createFromParcel(parcel: Parcel): Announcement {
            return Announcement(parcel)
        }

        override fun newArray(size: Int): Array<Announcement?> {
            return arrayOfNulls(size)
        }

        fun hasLatestAnnouncement(context: Context, announcements: Array<Announcement>): Boolean {
            val sharedPreferences = context.getSharedPreferences("static", Context.MODE_PRIVATE)
            if (announcements.isEmpty()) {
                return false
            }
            val isReadAnnouncementPostDate = sharedPreferences.getLong("is_read_announcement_post_date",-1)
            if(isReadAnnouncementPostDate == -1L){
                return true
            }
            return announcements.sortedByDescending { it.postDate }.first().postDate > isReadAnnouncementPostDate
        }
        fun updateLatestAnnouncement(context: Context, announcements: Array<Announcement>){
            val sharedPreferences = context.getSharedPreferences("static", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            if (announcements.isEmpty()) {
                return
            }
            editor.putLong("is_read_announcement_post_date",announcements.sortedByDescending { it.postDate }.first().postDate)
            editor.apply()

        }
    }
}