package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class AutofitRecyclerView:RecyclerView {
    private var gridManager:GridLayoutManager? = null
    private var columnWidth = -1
    constructor(context: Context) : super(context){
        init(context,null)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(context,attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle){
        init(context,attrs)
    }
    private fun init(context: Context?,attrs: AttributeSet?){
        if(attrs !=null){
            val attrsArray = intArrayOf(android.R.attr.columnWidth)
            val array = context!!.obtainStyledAttributes(attrs,attrsArray)
            this.columnWidth = array.getDimensionPixelSize(0,-1)
            array.recycle()

        }
        this.gridManager = GridLayoutManager(context,1)
        layoutManager = this.gridManager
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if(this.columnWidth > 0){
            val spanCount = Math.max(1,measuredWidth/columnWidth)
            this.gridManager!!.spanCount = spanCount
        }
    }
}