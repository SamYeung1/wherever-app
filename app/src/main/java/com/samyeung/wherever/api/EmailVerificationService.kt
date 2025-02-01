package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.ResponsePagingResult
import com.samyeung.wherever.model.ResponseResult
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.model.UserProfile
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface EmailVerificationService {
    @POST("authorize/verifySend")
    fun verifySend(@Query("resend") resend: String): Observable<ResponseResult<Any?>>

    @POST("authorize/verify")
    fun verify(@Body verificationBody: VerificationBody): Observable<ResponseResult<Any?>>

    data class VerificationBody(@SerializedName("code") val code: String) :
        Parcelable {
        constructor(parcel: Parcel) : this( parcel.readString()!!) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(code)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<VerificationBody> {
            override fun createFromParcel(parcel: Parcel): VerificationBody {
                return VerificationBody(parcel)
            }

            override fun newArray(size: Int): Array<VerificationBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
