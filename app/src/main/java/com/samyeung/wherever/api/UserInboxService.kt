package com.samyeung.wherever.api

import com.samyeung.wherever.model.ResponsePagingResult
import com.samyeung.wherever.model.ResponseResult
import com.samyeung.wherever.model.UserInbox
import io.reactivex.Observable
import retrofit2.http.*

interface UserInboxService {
    @GET("inbox")
    fun getInboxes(@Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<UserInbox>>>

    @PUT("inbox/{inbox_id}")
    fun updateInboxRead(@Part("inbox_id") inbox_id: String): Observable<ResponseResult<UserInbox?>>
    @PUT("inbox")
    fun updateInboxReadAll(): Observable<ResponseResult<UserInbox?>>

    @GET("inbox/{inbox_id}")
    fun getInbox(@Part("inbox_id") inbox_id: String): Observable<ResponseResult<UserInbox>>
}
