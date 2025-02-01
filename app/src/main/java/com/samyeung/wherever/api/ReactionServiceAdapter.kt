package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.PagingModel
import com.samyeung.wherever.model.Reaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class ReactionServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val reactionService by lazy {
        RetrofitInstance.createReactionService(context)
    }

    fun insertReaction(traceId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createReactionService(context).postReaction(ReactionService.ReactionBody(traceId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onInsertReaction()
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onInsertReaction() {

    }

    fun deleteReaction(traceId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createReactionService(context).deleteReaction(ReactionService.ReactionBody(traceId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onDeleteReaction()
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onDeleteReaction() {

    }

    fun getReactions(traceId: String, page: Int, limit: Int = 10) {
        this.compositeDisposable.add(
            RetrofitInstance.createReactionService(context).getReactions(traceId, page, limit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                onGetReactions(result.result)
            }, { e ->
                error(e)
            })
        )
    }

    open fun onGetReactions(reactions: PagingModel<Array<Reaction>>) {

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}