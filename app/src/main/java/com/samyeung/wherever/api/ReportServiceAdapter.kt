package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class ReportServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val reportService by lazy {
        RetrofitInstance.createReportService(context)
    }

    fun sendReport(sourceId: String,type:String,message:String) {
        this.compositeDisposable.add(
            RetrofitInstance.createReportService(context)
                .sendReport(ReportService.ReportBody(sourceId,type,message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onReportRequest()
                }, { e ->
                    if (canRetry()) {
                        this.sendReport(sourceId, type, message)
                    }
                    error(e)
                })
        )
    }

    open fun onReportRequest() {

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}