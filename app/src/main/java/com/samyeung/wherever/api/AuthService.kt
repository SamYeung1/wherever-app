package com.samyeung.wherever.api

import com.google.gson.annotations.SerializedName
import com.samyeung.wherever.model.ResponseResult
import com.samyeung.wherever.model.Token
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("authorize/login")
    fun login(@Body account: AccountBody): Observable<ResponseResult<Token>>

    @POST("authorize/register")
    fun register(@Body account: RegisterBody): Observable<ResponseResult<Any?>>

    @POST("authorize/refresh")
    fun refresh(@Body account: RefreshTokenBody): Call<ResponseResult<Token>>

    @POST("authorize/refresh")
    fun refreshWithObservable(@Body account: RefreshTokenBody): Observable<ResponseResult<Token>>

    @POST("authorize/forgotPassword")
    fun forgotPassword(@Body forgotPasswordBody: ForgotPasswordBody): Observable<ResponseResult<Any?>>

    @POST("authorize/changePassword")
    fun changePassword(@Body changePasswordBody: ChangePasswordBody): Observable<ResponseResult<Any?>>

    @POST("authorize/logout")
    fun logout(): Observable<ResponseResult<Any?>>
}

open class AccountBody(@SerializedName("email")var email: String,@SerializedName("password") var password: String)
open class RegisterBody(@SerializedName("email")var email: String,@SerializedName("password") var password: String,@SerializedName("display_name") var display_name: String)
open class RefreshTokenBody(@SerializedName("refresh_token")var refresh_token: String)
open class ForgotPasswordBody(@SerializedName("email")var email: String)
open class ChangePasswordBody(@SerializedName("new_password")var new_password: String,@SerializedName("current_password") var current_password: String)