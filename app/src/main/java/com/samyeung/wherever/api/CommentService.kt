package com.samyeung.wherever.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.*
import io.reactivex.Observable
import retrofit2.http.*

interface CommentService {
    @GET("comment")
    fun getComments(@Query("trace_id") traceId: String, @Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<Comment>>>

    @DELETE("comment/{comment_id}")
    fun deleteComment(@Path("comment_id") comment_id: String): Observable<ResponseResult<Comment>>

    @GET("comment/{comment_id}")
    fun getComment(@Path("comment_id") comment_id: String): Observable<ResponseResult<Comment>>

    @POST("comment")
    fun postComment(@Body commentBody: CommentBody): Observable<ResponseResult<Comment>>

    @HTTP(path = "comment/{comment_id}", method = "PUT", hasBody = true)
    fun updateComment(@Path("comment_id") comment_id: String, @Body commentBody: UpdateCommentBody): Observable<ResponseResult<Comment>>

    data class CommentBody(@SerializedName("trace_id") val trace_id: String,@SerializedName("message") val message: String) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(trace_id)
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<CommentBody> {
            override fun createFromParcel(parcel: Parcel): CommentBody {
                return CommentBody(parcel)
            }

            override fun newArray(size: Int): Array<CommentBody?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class UpdateCommentBody(val message: String):Parcelable {
        constructor(parcel: Parcel) : this(parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<UpdateCommentBody> {
            override fun createFromParcel(parcel: Parcel): UpdateCommentBody {
                return UpdateCommentBody(parcel)
            }

            override fun newArray(size: Int): Array<UpdateCommentBody?> {
                return arrayOfNulls(size)
            }
        }
    }
}
