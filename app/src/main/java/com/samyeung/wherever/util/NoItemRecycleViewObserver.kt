package com.samyeung.wherever.util

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

class NoItemRecycleViewObserver(private val view: View, private val recycleViewAdapter: RecyclerView.Adapter<*>) :
    RecyclerView.AdapterDataObserver() {
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        super.onItemRangeChanged(positionStart, itemCount, payload)
        Log.d("NoItemObserver", "onItemRangeChanged ")
        view.visibility = if (recycleViewAdapter.itemCount > 0) View.INVISIBLE else View.VISIBLE
    }
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        view.visibility = if (recycleViewAdapter.itemCount > 0) View.INVISIBLE else View.VISIBLE
        Log.d("NoItemObserver", "onItemRangeInserted ")

    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        view.visibility = if (recycleViewAdapter.itemCount > 0) View.INVISIBLE else View.VISIBLE
        Log.d("NoItemObserver", "onItemRangeRemoved ")
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        view.visibility = if (recycleViewAdapter.itemCount > 0) View.INVISIBLE else View.VISIBLE
        Log.d("NoItemObserver", "onItemRangeChanged ")
    }

    override fun onChanged() {
        super.onChanged()
        Log.d("NoItemObserver", "onChanged ")
        view.visibility = if (recycleViewAdapter.itemCount > 0) View.INVISIBLE else View.VISIBLE
    }
}