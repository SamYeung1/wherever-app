package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface FeedbackService {
    @POST("feedback")
    fun sendFeedback(@Body reportBody: FeedbackBody): Observable<ResponseResult<Any?>>

    data class FeedbackBody(@SerializedName("message") val message:String) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FeedbackBody> {
            override fun createFromParcel(parcel: Parcel): FeedbackBody {
                return FeedbackBody(parcel)
            }

            override fun newArray(size: Int): Array<FeedbackBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
