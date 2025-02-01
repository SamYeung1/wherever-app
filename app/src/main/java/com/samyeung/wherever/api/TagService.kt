package com.samyeung.wherever.api

import com.samyeung.wherever.model.ResponseResult
import com.samyeung.wherever.model.Tag
import io.reactivex.Observable
import retrofit2.http.*

interface TagService {
    @GET("tag")
    fun getTags(@Query("q") q:String): Observable<ResponseResult<Array<Tag>>>

}
