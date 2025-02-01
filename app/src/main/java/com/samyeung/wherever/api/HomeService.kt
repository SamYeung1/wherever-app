package com.samyeung.wherever.api

import com.samyeung.wherever.model.Home
import com.samyeung.wherever.model.ResponseResult
import io.reactivex.Observable
import retrofit2.http.*

interface HomeService {
    @GET("home")
    fun getHome(): Observable<ResponseResult<Home>>
}
