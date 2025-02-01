package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class EmailVerificationServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val emailService by lazy {
        RetrofitInstance.createEmailService(context)
    }

    fun verifySend(resend: Boolean) {
        this.compositeDisposable.add(
            RetrofitInstance.createEmailService(context).verifySend(if (resend) "1" else "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onVerifySend()
                }, { e ->
                    if (canRetry()) {
                        this.verifySend(resend)
                    }
                    error(e)
                })
        )
    }

    open fun onVerifySend() {
    }

    fun verify(code: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createEmailService(context).verify(EmailVerificationService.VerificationBody(code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onVerify()
                }, { e ->
                    if (canRetry()) {
                        this.verify(code)
                    }
                    error(e)
                })
        )
    }

    open fun onVerify() {
    }


    fun onDestroy() {
        compositeDisposable.clear()
    }

}