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

interface UserService {
    @GET("profile")
    fun findProfiles(@Query("q") q: String, @Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<UserProfile>>>

    @GET("profile/{user_id}/trace")
    fun getTraces(@Path("user_id") userId: String, @Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<Trace>>>

    @GET("profile/{user_id}")
    fun getProfile(@Path("user_id") userId: String): Observable<ResponseResult<UserProfile>>

    @PUT("profile/icon")
    @Multipart
    fun updateIcon(@Part body: MultipartBody.Part): Observable<ResponseResult<UserProfile>>

    @PUT("profile")
    fun updateProfile(@Body profile: UserProfileBody): Observable<ResponseResult<Any?>>

    @PUT("profile/profileIcon")
    @Multipart
    fun updateProfileIcon(@Part body: MultipartBody.Part): Observable<ResponseResult<UserProfile>>

    @DELETE("profile/icon")
    fun removeIcon(): Observable<ResponseResult<UserProfile>>

    @DELETE("profile/profileIcon")
    fun removeProfileIcon(): Observable<ResponseResult<UserProfile>>

    data class UserProfileBody(@SerializedName("display_name") val display_name: String = "", @SerializedName("about_me") val about_me: String = "") :
        Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readString()!!) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(display_name)
            parcel.writeString(about_me)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<UserProfileBody> {
            override fun createFromParcel(parcel: Parcel): UserProfileBody {
                return UserProfileBody(parcel)
            }

            override fun newArray(size: Int): Array<UserProfileBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
