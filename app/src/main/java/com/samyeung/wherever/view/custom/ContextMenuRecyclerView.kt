package com.samyeung.wherever.view.custom

import android.content.Context
import android.view.ContextMenu
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet


class ContextMenuRecyclerView : RecyclerView {

    private var mContextMenuInfo: ContextMenu.ContextMenuInfo? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun getContextMenuInfo(): ContextMenu.ContextMenuInfo? {
        return mContextMenuInfo
    }

    /**
     * Used to initialize before creating context menu and Bring up the context menu for this view.
     *
     * @param position for ContextMenuInfo
     */
    fun openContextMenu(position: Int, data:Any?) {
        if (position >= 0) {
            val childId = adapter!!.getItemId(position)
            mContextMenuInfo = createContextMenuInfo(position, childId, data)
        }
        showContextMenu()
    }


    private fun createContextMenuInfo(position: Int, id: Long,data:Any?): ContextMenu.ContextMenuInfo {
        return RecyclerContextMenuInfo(position, id, data)
    }

    /**
     * Extra menu information provided to the [ .OnCreateContextMenuListener#onCreateContextMenu(android.view.ContextMenu, View,][android.view.View] callback when a context menu is brought up for this RecycleView.
     */
    class RecyclerContextMenuInfo(
        /**
         * The position in the adapter for which the context menu is being displayed.
         */
        var position: Int,
        /**
         * The row id of the item for which the context menu is being displayed.
         */
        var id: Long,

        var data:Any?
    ) : ContextMenu.ContextMenuInfo

}