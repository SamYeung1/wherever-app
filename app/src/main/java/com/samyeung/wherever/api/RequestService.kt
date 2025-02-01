package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.Request
import com.samyeung.wherever.model.ResponsePagingResult
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface RequestService {
    @POST("request/friend")
    fun sendFriendRequest(@Body friendRequestBody: FriendRequestBody): Observable<ResponseResult<Request?>>

    @POST("request/friend/cancel")
    fun cancelFriendRequest(@Body friendRequestBody: FriendRequestBody): Observable<ResponseResult<Request?>>

    @HTTP(method = "PUT", path = "request/friend", hasBody = true)
    fun acceptFriendRequest(@Body requestBody: RequestBody): Observable<ResponseResult<Request?>>

    @HTTP(method = "DELETE", path = "request/friend", hasBody = true)
    fun denyFriendRequest(@Body requestBody: RequestBody): Observable<ResponseResult<Request?>>

    @GET("request")
    fun getRequests(@Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<Request>>>

    data class FriendRequestBody(@SerializedName("user_id") val user_id: String) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(user_id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FriendRequestBody> {
            override fun createFromParcel(parcel: Parcel): FriendRequestBody {
                return FriendRequestBody(parcel)
            }

            override fun newArray(size: Int): Array<FriendRequestBody?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class RequestBody(val request_id: String = "", val user_id: String = "") : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(request_id)
            parcel.writeString(user_id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FriendRequestBody> {
            override fun createFromParcel(parcel: Parcel): FriendRequestBody {
                return FriendRequestBody(parcel)
            }

            override fun newArray(size: Int): Array<FriendRequestBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
