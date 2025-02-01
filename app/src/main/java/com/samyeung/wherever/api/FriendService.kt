package com.samyeung.wherever.api

import com.samyeung.wherever.model.ResponsePagingResult
import com.samyeung.wherever.model.ResponseResult
import com.samyeung.wherever.model.UserProfile
import io.reactivex.Observable
import retrofit2.http.*

interface FriendService {
    @GET("friend")
    fun getFriends(@Query("p") page: Int, @Query("limit") limit: Int): Observable<ResponsePagingResult<Array<UserProfile>>>

    @HTTP(method = "DELETE", path = "friend/{user_id}", hasBody = true)
    fun deleteFriend(@Path("user_id") user_id: String): Observable<ResponseResult<UserProfile?>>

}
