package com.samyeung.wherever.api

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.util.helper.DateManager
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ImageTraceService {
    @GET("trace_image/{trace_id}")
    fun getTrace(@Path("trace_id") traceId: String): Observable<ResponseResult<Trace>>

    @GET("trace_image")
    fun getTraces(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("distance") distance: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("user_ids") userIds: String?,
        @Query("tags") tags: String?
    ): Observable<ResponseResult<Array<Trace>>>

    @DELETE("trace_image/{trace_id}")
    fun deleteTrace(@Path("trace_id") traceId: String): Observable<ResponseResult<Trace>>

    @PUT("trace_image/{trace_id}")
    fun updateTrace(@Path("trace_id") traceId: String, @Body traceBody: UpdateTraceBody): Observable<ResponseResult<Trace>>

    @POST("trace_image")
    @Multipart
    fun insertTrace(
        @Part src: Array<MultipartBody.Part>,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("tags") tags: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody
    ): Flowable<ResponseResult<Trace?>>

    data class UpdateTraceBody(
        @SerializedName("title") var title: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("longitude") var longitude: Double? = null,
        @SerializedName("latitude") var latitude: Double? = null,
        @SerializedName("tags") var tags: Array<String>? = null
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.createStringArray()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeValue(longitude)
            parcel.writeValue(latitude)
            parcel.writeStringArray(tags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<UpdateTraceBody> {
            override fun createFromParcel(parcel: Parcel): UpdateTraceBody {
                return UpdateTraceBody(parcel)
            }

            override fun newArray(size: Int): Array<UpdateTraceBody?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class TraceBody(
        val src: Uri,
        val title: String,
        val description: String,
        val tags: Array<String>,
        val longitude: Double,
        val latitude: Double,
        val token_date:Long = DateManager.toDay
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArray(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readLong()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(src, flags)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeStringArray(tags)
            parcel.writeDouble(longitude)
            parcel.writeDouble(latitude)
            parcel.writeLong(token_date)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<TraceBody> {
            override fun createFromParcel(parcel: Parcel): TraceBody {
                return TraceBody(parcel)
            }

            override fun newArray(size: Int): Array<TraceBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
