package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.Reaction
import com.samyeung.wherever.model.ResponsePagingResult
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface ReactionService {
    @GET("reaction")
    fun getReactions(@Query("trace_id") traceId: String, @Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<Reaction>>>

    @HTTP(path = "reaction", method = "DELETE", hasBody = true)
    fun deleteReaction(@Body reactionBody: ReactionBody): Observable<ResponseResult<Reaction?>>

    @POST("reaction")
    fun postReaction(@Body reactionBody: ReactionBody): Observable<ResponseResult<Reaction?>>


    data class ReactionBody(@SerializedName("trace_id") val trace_id: String) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(trace_id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ReactionBody> {
            override fun createFromParcel(parcel: Parcel): ReactionBody {
                return ReactionBody(parcel)
            }

            override fun newArray(size: Int): Array<ReactionBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
