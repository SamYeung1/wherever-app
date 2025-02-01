package com.samyeung.wherever.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

/*
 "id": "5d2880893bb25568448a5c53",
                "source": {
                    "id": "5ccd3725c0a67504084ab875",
                    "display_name": "Too Long Text Sam",
                    "account_id": "sam_yeung",
                    "icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/icon_1562252486545.jpg",
                    "profile_icon": "https://whereverdev.s3.amazonaws.com/profile/5ccd3725c0a67504084ab875/profile_icon_1562865997743.jpg",
                    "about_me": null
                },
                "type": "FRIEND",
                "post_date": 1562935433104
 */
data class Request(
    @SerializedName("id") val id:String,
    @SerializedName("source") val source: LinkedTreeMap<String,Any>,
    @SerializedName("user") val user:UserProfile,
    @SerializedName("type") val type:String,
    @SerializedName("post_date") val postDate:Long
){

    companion object{
        val TYPE_FRIEND = "FRIEND"
    }
}