package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.Comment
import com.samyeung.wherever.model.PagingModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class CommentServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val commentService by lazy {
        RetrofitInstance.createCommentService(context)
    }

    fun insertComment(traceId: String, message: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createCommentService(context).postComment(CommentService.CommentBody(traceId, message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onInsertComment(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onInsertComment(comment: Comment) {

    }

    fun deleteComment(commentId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createCommentService(context).deleteComment(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onDeleteComment(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onDeleteComment(comment: Comment) {

    }

    fun updateComment(commentId: String, message: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createCommentService(context).updateComment(
                commentId,
                CommentService.UpdateCommentBody(message)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onUpdateComment(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onUpdateComment(comment: Comment) {

    }

    fun getComments(traceId: String, page: Int, limit: Int = 10) {
        this.compositeDisposable.add(
            RetrofitInstance.createCommentService(context).getComments(traceId, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                        onGetComments(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onGetComments(comments: PagingModel<Array<Comment>>) {

    }

    fun getComment(commentId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createCommentService(context).getComment(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetComment(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onGetComment(comment: Comment) {

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}