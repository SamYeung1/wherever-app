package com.samyeung.wherever.api

import android.content.Context
import android.util.Log
import com.samyeung.wherever.MainActivity
import com.samyeung.wherever.R
import com.samyeung.wherever.util.ErrorException
import com.samyeung.wherever.util.LoginManager
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.fragment.EmailVerificationFragment
import com.samyeung.wherever.fragment.auth.LoginFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import retrofit2.HttpException

abstract class APIController(private val context: Context) {
    protected open var RETRY_LIMIT = 5
    private var retryCount = 0
    protected open fun error(e: Throwable) {
        retryCount++
        Log.d("API Error", e.toString())
        e.printStackTrace()
        when (e) {
            is java.net.UnknownHostException ->{
                onError(ErrorException(APIMapping.CODE_NETWORK_ERROR,e.message.toString()))
            }
            is ErrorException -> {
                onError(e)
            }
            is HttpException -> {
                if (e.code() == 401) {
                    onError(ErrorException(e.code(), e.message()))
                }
            }
        }

    }

    protected fun canRetry(): Boolean {
        return retryCount <= RETRY_LIMIT
    }
    protected fun resetRetry(){
        this.retryCount = 0
    }

    open fun onError(e: ErrorException) {
        if (e.retcode == APIMapping.CODE_AUTH_ERROR || e.retcode == APIMapping.CODE_ACCOUNT_BLACK_LIST_ERROR) {
            if (context is MainActivity) {
                FragmentUtil.setFragment(null,context,R.id.content,LoginFragment.newInstance(),MainActivity.FRG_TAG_MAIN,true)
                LoginManager.logout(context)
                this.retryCount = RETRY_LIMIT + 1
            }
        }else if(e.retcode == APIMapping.CODE_EMAIL_VERIFICATION_ERROR){
            if (context is MainActivity) {
                FragmentUtil.setFragment(null,context,R.id.content,EmailVerificationFragment.newInstance(),MainActivity.FRG_TAG_MAIN,true)
                this.retryCount = RETRY_LIMIT + 1
            }
        }
    }
}