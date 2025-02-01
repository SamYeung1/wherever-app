package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samyeung.wherever.R
import com.samyeung.wherever.view.custom.ItemView4

abstract class HeaderAndItemViewRecycleViewAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView, @LayoutRes private val headerLayout: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var viewModel: ViewModel

    init {
        this.viewModel = ViewModel()
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            HeaderViewHolder.VIEW_TYPE ->
                onCreateHeaderViewHolder(
                    parent.inflate(
                        this.headerLayout
                    )
                )
            else ->
                ItemViewHolder(
                    context,
                    parent.inflate(
                        R.layout.layout_list_item_1
                    )
                )
        }

    override fun getItemCount(): Int = this.viewModel.getCount()

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) =
        when (p0) {
            is HeaderViewHolder -> {
                p0.bind(this.viewModel.getItems()[position])
            }
            is ItemViewHolder -> {
                p0.bind(this.viewModel.getItems()[position] as BodyItem)
            }
            else -> {

            }
        }


    override fun getItemViewType(position: Int): Int =
        when (position) {
            viewModel.HEADER_PROSITION -> {
                HeaderViewHolder.VIEW_TYPE
            }
            else -> {
                ItemViewHolder.VIEW_TYPE
            }
        }

    fun addItem(item: BodyItem) {
        this.viewModel.getItems().add(item)
        notifyItemInserted(itemCount)
    }

    fun refreshHeader() {
        this.notifyItemChanged(0)
    }

    abstract fun createHeaderItem(): Item
    abstract fun onCreateHeaderViewHolder(view: View): HeaderViewHolder
    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

    private inner class ViewModel {
        val HEADER_PROSITION = 0
        private var items = arrayListOf<Item>()

        init {
            this.items.add(HEADER_PROSITION, this@HeaderAndItemViewRecycleViewAdapter.createHeaderItem())
        }

        fun getItems() = this.items
        fun getCount(): Int = this.items.size
    }

    private class ItemViewHolder(val context: Context, view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            val VIEW_TYPE = 1
        }

        init {
            itemView.foreground = ContextCompat.getDrawable(context, R.drawable.bg_selectable_menu_1)
            itemView.isClickable = true
            itemView.isFocusable = true
        }

        fun bind(item: BodyItem) {
            itemView.findViewById<ItemView4>(R.id.item_view).icon = ContextCompat.getDrawable(context, item.icon)
            itemView.findViewById<ItemView4>(R.id.item_view).title = item.title
            itemView.setOnClickListener {
                item.onItemClick?.invoke(it)
            }
            //itemView.findViewById<ItemView4>(R.id.item_view).value= item.value
        }
    }

    data class BodyItem(
        @DrawableRes val icon: Int, val title: String,
        val value: String,
        val onItemClick: ((View) -> Unit)? = null
    ) : Item

    interface Item
    abstract class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            val VIEW_TYPE = 0
        }

        abstract fun bind(item: Item)
    }
}

