package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.util.helper.LinearEndlessRecyclerOnScrollListener
import com.samyeung.wherever.model.Announcement
import kotlinx.android.synthetic.main.layout_list_announcement.view.*
import kotlinx.android.synthetic.main.layout_list_comment.view.img_primary_icon


class AnnouncementListAdapter(
    val context: Context,
    val recyclerView: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }

    private val list = mutableListOf<Item>()
    var onItemClick: ((Announcement) -> Unit)? = null

    inner class TraceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(i: Item) {
            i.announcement.let { item ->
                if (item.banner != null) {
                    itemView.img_primary_icon.visibility = View.VISIBLE
                    ImageUtil.loadImage(
                        context,
                        itemView.img_primary_icon,
                        item.banner,
                        ImageUtil.createCenterCropRequestOption().override(500)
                    )
                } else {
                    itemView.img_primary_icon.visibility = View.GONE
                }
                itemView.tv_title.text = item.title
                itemView.setOnClickListener { onItemClick?.invoke(item) }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TraceViewHolder(parent.inflate(R.layout.layout_list_announcement))


    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TraceViewHolder) {
            holder.bind(list[position])
        }
    }


    fun add(announcement: Announcement) {
        list.add(Item(announcement))
        notifyItemInserted(list.size - 1)
    }

    fun update(announcement: Announcement) {
        val item = list.find { it.announcement.id == announcement.id }
        val index = list.indexOf(list.find { it.announcement.id == announcement.id })
        item!!.announcement = announcement
        list.set(index, item)
        item.isUpdating = false
        notifyItemChanged(index)
    }

    fun addAll(announcements: Array<Announcement>) {
        list.addAll(announcements.map { return@map Item(it) }.filter { return@filter list.find { item -> item.announcement.id == it.announcement.id } == null })
        notifyDataSetChanged()
    }

    fun remove(id: String) {
        val item = list.find { it.announcement.id == id }
        val index = list.indexOf(item)
        list.removeAt(index)
        notifyItemRemoved(index)
    }


    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun initLoadMore(func: (Int) -> Unit) {
        recyclerView.addOnScrollListener(object :
            LinearEndlessRecyclerOnScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                func(currentPage)
            }

        })
    }

    fun getItems(): List<Announcement> {
        return this.list.map { return@map it.announcement }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

    data class Item(var announcement: Announcement, var isDelete: Boolean = false, var isUpdating: Boolean = false)
}