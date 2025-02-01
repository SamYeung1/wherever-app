package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.LoginManager
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.Token
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class AuthServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val authService by lazy {
        RetrofitInstance.createAuthService(context)
    }

    fun login(email: String, password: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createAuthService(context).login(AccountBody(email, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onLogin(result.result, result.time)
                }, { e -> error(e) })
        )
    }


    open fun onLogin(result: Token, lastLoginTime: Long) {

    }

    fun logout() {
        this.compositeDisposable.add(
            RetrofitInstance.createAuthService(context).logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onLogout()
                }, { e -> error(e) })
        )
    }


    open fun onLogout() {
    }

    fun refreshToken() {
        val refreshToken =
            if (LoginManager.getRefreshToken(context) == null) "" else LoginManager.getRefreshToken(context)!!
        this.compositeDisposable.add(
            RetrofitInstance.createAuthService(context, false).refreshWithObservable(RefreshTokenBody(refreshToken))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    LoginManager.setUp(context, result.result)
                    onRefreshTokenSucceed()
                }, { e -> error(e) })
        )
    }

    open fun onRefreshTokenSucceed() {

    }

    fun register(body: RegisterBody) {
        this.compositeDisposable.add(
            RetrofitInstance.createAuthService(context).register(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onRegister()
                }, { e -> error(e) })
        )
    }

    open fun onRegister() {

    }

    fun forgotPassword(email: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createAuthService(context).forgotPassword(ForgotPasswordBody(email))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onForgotPassword()
                }, { e -> error(e) })
        )
    }

    open fun onForgotPassword() {

    }

    fun changePassword(currentPassword: String, newPassword: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createAuthService(context,true).changePassword(ChangePasswordBody(newPassword, currentPassword))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onChangePassword()
                }, { e -> error(e) })
        )
    }

    open fun onChangePassword() {

    }

    fun onDestroy() {
        this.compositeDisposable.clear()
    }

}