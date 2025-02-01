package com.samyeung.wherever.util.helper

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView


abstract class GridEndlessRecyclerOnScrollListener(private val mGridLayoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {

    private val VISIBLE_THRESHOLD = 10

    private var mPreviousTotal = 0
    private var mLoading = true
    private var mCurrentPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView!!.childCount
        val totalItemCount = mGridLayoutManager.itemCount
        val firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition()

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false
                mPreviousTotal = totalItemCount
            }
        }
        if (!mLoading && totalItemCount - firstVisibleItem - visibleItemCount <= VISIBLE_THRESHOLD) {

            onLoadMore(++mCurrentPage)

            mLoading = true
        }
    }

    fun reset() {
        mCurrentPage = 1
        mPreviousTotal = 0
        mLoading = true
    }

    abstract fun onLoadMore(currentPage: Int)

    companion object {

        var LOG_TAG = GridEndlessRecyclerOnScrollListener::class.java.simpleName
    }
}