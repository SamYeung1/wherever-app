package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.AppVersion
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface AppVersionService {
    @POST("version")
    fun checkVersion(@Body appVersionBody:AppVersionBody): Observable<ResponseResult<AppVersion>>
    data class AppVersionBody(@SerializedName("current_version")var current_version:Int,@SerializedName("platform") var platform:String):Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(current_version)
            parcel.writeString(platform)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AppVersionBody> {
            override fun createFromParcel(parcel: Parcel): AppVersionBody {
                return AppVersionBody(parcel)
            }

            override fun newArray(size: Int): Array<AppVersionBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
