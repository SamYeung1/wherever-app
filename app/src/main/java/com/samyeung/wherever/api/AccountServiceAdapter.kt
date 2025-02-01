package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class AccountServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val authService by lazy {
        RetrofitInstance.createAuthService(context, true)
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

    fun onDestroy() {
        this.compositeDisposable.clear()
    }


}