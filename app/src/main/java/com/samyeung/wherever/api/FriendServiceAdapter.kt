package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.UserProfile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class FriendServiceAdapter(val context: Context):APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val friendService by lazy {
        RetrofitInstance.createFriendService(context)
    }
    fun getFriends(page: Int, limit: Int = 10) {
        this.compositeDisposable.add(
            RetrofitInstance.createFriendService(context).getFriends(page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetFriends(result.result.data)
                }, { e ->
                    error(e)
                })
        )
    }
    open fun onGetFriends(friends:Array<UserProfile>){

    }
    fun deleteFriend(userId:String) {
        this.compositeDisposable.add(
            RetrofitInstance.createFriendService(context).deleteFriend(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onDeleteFriend(result.result)
                }, { e ->
                    error(e)
                })
        )
    }
    open fun onDeleteFriend(friend:UserProfile? = null){

    }
    fun onDestroy(){
        this.compositeDisposable.clear()
    }

}
