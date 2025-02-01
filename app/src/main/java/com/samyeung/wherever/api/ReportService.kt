package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface ReportService {
    @POST("report")
    fun sendReport(@Body reportBody: ReportBody): Observable<ResponseResult<Any?>>

    data class ReportBody(@SerializedName("source_id") val source_id: String,@SerializedName("type") val type:String,@SerializedName("message") val message:String) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(source_id)
            parcel.writeString(type)
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ReportBody> {
            override fun createFromParcel(parcel: Parcel): ReportBody {
                return ReportBody(parcel)
            }

            override fun newArray(size: Int): Array<ReportBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
