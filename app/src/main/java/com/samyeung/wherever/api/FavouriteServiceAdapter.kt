package com.samyeung.wherever.api

import android.content.Context
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.model.PagingModel
import com.samyeung.wherever.model.Trace
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class FavouriteServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val favouriteService by lazy {
        RetrofitInstance.createFavouriteService(context)
    }

    fun addFavourite(traceId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createFavouriteService(context).addFavourite(FavouriteService.FavouriteBody(traceId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onAddFavourite(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onAddFavourite(favourite:Trace?) {

    }

    fun removeFavourite(traceId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createFavouriteService(context).removeFavourite(FavouriteService.FavouriteBody(traceId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onRemoveFavourite(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onRemoveFavourite(favourite:Trace?) {

    }

    fun getFavourites(page: Int, limit: Int = 10) {
        this.compositeDisposable.add(
            RetrofitInstance.createFavouriteService(context).getFavourites(page, limit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                onGetFavourites(result.result)
            }, { e ->
                error(e)
            })
        )
    }

    open fun onGetFavourites(favourites: PagingModel<Array<Trace>>) {

    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}