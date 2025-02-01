package com.samyeung.wherever.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.MainThread
import com.google.gson.annotations.SerializedName

data class Home(
    @SerializedName("number_of_inbox") val numberOfInbox:Int,
    @SerializedName("announcements") val announcements:Array<Announcement>,
    @SerializedName("number_of_request") val numberOfRequest:Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createTypedArray(Announcement),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numberOfInbox)
        parcel.writeTypedArray(announcements, flags)
        parcel.writeInt(numberOfRequest)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Home> {
        override fun createFromParcel(parcel: Parcel): Home {
            return Home(parcel)
        }

        override fun newArray(size: Int): Array<Home?> {
            return arrayOfNulls(size)
        }
    }
    class HomeLiveData : MutableLiveData<Home>() {
        fun update(home: Home) {
            this.value =home
        }

        companion object {
            private var sInstance: HomeLiveData? = null

            @MainThread
            @JvmStatic
            fun get(): HomeLiveData {
                if (sInstance == null) {
                    sInstance = HomeLiveData()
                }
                return sInstance!!
            }
            fun clear(){
                this.sInstance = null
            }
        }
    }
}
