package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.Tag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class TagServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val tagService by lazy {
        RetrofitInstance.createTagService(context)
    }

    fun getTags(q:String) {
        this.compositeDisposable.add(
            RetrofitInstance.createTagService(context).getTags(q)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetTags(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.getTags(q)
                    }
                    error(e)
                })
        )
    }

    open fun onGetTags(tags:Array<Tag>){

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}