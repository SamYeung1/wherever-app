package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.Home
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class HomeServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val homeService by lazy {
        RetrofitInstance.createHomeService(context)
    }

    fun getHome() {
        this.compositeDisposable.add(
            RetrofitInstance.createHomeService(context).getHome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetHome(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.getHome()
                    }
                    error(e)
                })
        )
    }

    open fun onGetHome(result:Home){

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}