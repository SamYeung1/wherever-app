package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.ResponsePagingResult
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface FavouriteService {
    @GET("favourite")
    fun getFavourites(@Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<Trace>>>

    @HTTP(path = "favourite", method = "DELETE", hasBody = true)
    fun removeFavourite(@Body favouriteBody: FavouriteBody): Observable<ResponseResult<Trace?>>

    @HTTP(path = "favourite", method = "PUT", hasBody = true)
    fun addFavourite(@Body favouriteBody: FavouriteBody): Observable<ResponseResult<Trace?>>


    data class FavouriteBody(@SerializedName("trace_id") val trace_id: String) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(trace_id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FavouriteBody> {
            override fun createFromParcel(parcel: Parcel): FavouriteBody {
                return FavouriteBody(parcel)
            }

            override fun newArray(size: Int): Array<FavouriteBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
