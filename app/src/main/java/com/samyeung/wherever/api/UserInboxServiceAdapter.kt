package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.UserInbox
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class UserInboxServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val userInboxService by lazy {
        RetrofitInstance.createUserInboxService(context)
    }

    fun getInboxes(page: Int, limit: Int) {
        this.compositeDisposable.add(
            RetrofitInstance.createUserInboxService(context).getInboxes(page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetInboxes(result.result.data)
                }, { e ->
                    if (canRetry()) {
                        this.getInboxes(page, limit)
                    }
                    error(e)
                })
        )
    }

    open fun onGetInboxes(inboxes: Array<UserInbox>) {
    }

    fun getInbox(inboxId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createUserInboxService(context).getInbox(inboxId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetInbox(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.getInbox(inboxId)
                    }
                    error(e)
                })
        )
    }

    open fun onGetInbox(inbox: UserInbox) {
    }

    fun updateInboxRead(inboxId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createUserInboxService(context).updateInboxRead(inboxId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onUpdateInboxRead()
                }, { e ->
                    if (canRetry()) {
                        this.updateInboxRead(inboxId)
                    }
                    error(e)
                })
        )
    }

    open fun onUpdateInboxRead() {
    }

    fun updateInboxReadAll() {
        this.compositeDisposable.add(
            RetrofitInstance.createUserInboxService(context).updateInboxReadAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onUpdateInboxReadAll()
                }, { e ->
                    if (canRetry()) {
                        this.updateInboxReadAll()
                    }
                    error(e)
                })
        )
    }

    open fun onUpdateInboxReadAll() {
    }


    fun onDestroy() {
        compositeDisposable.clear()
    }

}