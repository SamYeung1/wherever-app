package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.AppVersion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class AppVersionServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val appVersionService by lazy {
        RetrofitInstance.createAppVersionService(context)
    }

    fun checkVersion() {
        this.compositeDisposable.add(
            RetrofitInstance.createAppVersionService(context).checkVersion(
                AppVersionService.AppVersionBody(
                    com.samyeung.wherever.BuildConfig.VERSION_CODE,
                    "ANDROID"
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onCheckVersion(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.checkVersion()
                    }
                    error(e)
                })
        )
    }

    open fun onCheckVersion(result: AppVersion) {

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}