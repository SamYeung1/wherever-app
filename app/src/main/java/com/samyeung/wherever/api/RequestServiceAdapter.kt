package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.Request
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers

open class RequestServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val requestService by lazy {
        RetrofitInstance.createRequestService(context)
    }

    fun sendFriendRequest(userId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createRequestService(context)
                .sendFriendRequest(RequestService.FriendRequestBody(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onSendFriendRequest()
                }, { e ->
                    if (canRetry()) {
                        this.sendFriendRequest(userId)
                    }
                    error(e)
                })
        )
    }

    open fun onSendFriendRequest() {

    }

    fun cancelFriendRequest(userId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createRequestService(context)
                .cancelFriendRequest(RequestService.FriendRequestBody(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onSendFriendRequest()
                }, { e ->
                    if (canRetry()) {
                        this.sendFriendRequest(userId)
                    }
                    error(e)
                })
        )
    }

    open fun onCancelFriendRequest() {

    }

    fun acceptFriendRequest(requestId: String = "",userId: String = "") {
        this.compositeDisposable.add(
            RetrofitInstance.createRequestService(context)
                .acceptFriendRequest(RequestService.RequestBody(requestId,userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onAcceptFriendRequest(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.acceptFriendRequest(requestId)
                    }
                    error(e)
                })
        )
    }

    open fun onAcceptFriendRequest(request: Request?) {

    }

    fun denyFriendRequest(requestId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createRequestService(context)
                .denyFriendRequest(RequestService.RequestBody(requestId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onDenyFriendRequest(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.denyFriendRequest(requestId)
                    }
                    error(e)
                })
        )
    }

    open fun onDenyFriendRequest(request: Request?) {

    }
    fun getRequests(page:Int,limit:Int = 10){
        try {
            this.compositeDisposable.add(
                RetrofitInstance.createRequestService(context)
                    .getRequests(page, limit)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        onGetRequests(result.result.data)
                    }, { e ->
                        if (canRetry()) {
                            this.getRequests(page, limit)
                        }
                        error(e)
                    })
            )
        }catch (e: CompositeException){
            this.getRequests(page,limit)
            this.compositeDisposable.clear()
        }

    }
    open fun onGetRequests(requests:Array<Request>){

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}