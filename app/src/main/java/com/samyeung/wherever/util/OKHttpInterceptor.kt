package com.samyeung.wherever.util

import android.content.Context
import android.util.Log
import com.samyeung.wherever.api.RefreshTokenBody
import com.samyeung.wherever.cst.APIMapping
import okhttp3.*
import java.util.concurrent.TimeUnit

class OKHttpInterceptor(private val context: Context, private val canCache: Boolean, private val lang: String) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        val request = chain!!.request()
        val requestBuilder = request.newBuilder()
        if (request.method().toString() == "GET") {
            if (canCache) {
                val cacheControl = CacheControl.Builder().maxAge(30, TimeUnit.DAYS).build()
                //requestBuilder.header("Cache-Control", "only-if-cached")
                requestBuilder.header("Cache-Control", cacheControl.toString())
            }
        }
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("x-api-key", APIMapping.API_KEY)
        requestBuilder.addHeader("x-lang", lang)
        LoginManager.getToken(context)?.let {
            requestBuilder.removeHeader("Authorization")
            requestBuilder.addHeader("Authorization", it)
        }

        val response = chain.proceed(requestBuilder.build())
        val responseBuilder = response!!.newBuilder()
        if (canCache) {
            val cacheControl = CacheControl.Builder().maxAge(30, TimeUnit.DAYS).build()
            responseBuilder.addHeader("Cache-Control", cacheControl.toString())
        }
        return responseBuilder.build()
    }

    class TokenAuthenticator(private val context: Context) : Authenticator {
        override fun authenticate(route: Route, response: Response): Request? {
            val authService = RetrofitInstance.createAuthService(context, false)
            try {
//
//               Log.d("token TokenExpiryTime", (LoginManager.getServerTime() >= LoginManager.getTokenExpiryTime()).toString())
//                Log.d("server Time", Date(LoginManager.getServerTime()).toString())
//                Log.d("token", LoginManager.getToken().toString())
//                if ((LoginManager.getRefreshToken(context) != null && LoginManager.getToken() == null) || LoginManager.getRefreshToken(
//                        context
//                    ) != null && (LoginManager.getServerTime() >= LoginManager.getTokenExpiryTime())
//                ) {
                if (response.code() == 401) {
                    val call = authService.refresh(RefreshTokenBody(LoginManager.getRefreshToken(context)!!))
                    val responseCall = call.execute()
                    val tokenResponse = responseCall.body()
                    tokenResponse?.let {
                        if (tokenResponse.resultCode == APIMapping.CODE_SUCCESS) {
                            LoginManager.setUp(context, it.result)
                        }
                    }
                }


            } catch (e: Exception) {
                Log.d("Refresh", LoginManager.getRefreshToken(context)!!)
                Log.d("Authenticator Error", e.toString())
                if (e is ErrorException) {
                    throw ErrorException(e.retcode, e.resmsg)
                }
            }

            return if (LoginManager.getToken(context) != null) {
                response.request().newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", LoginManager.getToken(context)!!)
                    .build()
            } else {
                null
            }

        }
    }
}