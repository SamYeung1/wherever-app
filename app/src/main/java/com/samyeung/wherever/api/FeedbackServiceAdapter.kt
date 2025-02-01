package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class FeedbackServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val reportService by lazy {
        RetrofitInstance.createFeedbackService(context)
    }

    fun sendFeedback(message:String) {
        this.compositeDisposable.add(
            RetrofitInstance.createFeedbackService(context)
                .sendFeedback(FeedbackService.FeedbackBody(message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onFeedback()
                }, { e ->
                    if (canRetry()) {
                        this.sendFeedback(message)
                    }
                    error(e)
                })
        )
    }

    open fun onFeedback() {

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}