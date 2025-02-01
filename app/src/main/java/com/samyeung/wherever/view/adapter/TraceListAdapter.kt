package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.signature.ObjectKey
import com.samyeung.wherever.R
import com.samyeung.wherever.util.NoItemRecycleViewObserver
import com.samyeung.wherever.util.helper.GridEndlessRecyclerOnScrollListener
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.model.Trace
import kotlinx.android.synthetic.main.layout_item_image.view.*


class TraceListAdapter(
    val context: Context,
    val recyclerView: RecyclerView,
    val linearLayoutHorizontal: Boolean = false,
    private val noItemView: View? = null
) : RecyclerView.Adapter<TraceListAdapter.ImageViewHolder>() {
    init {
        noItemView?.let {
            this.registerAdapterDataObserver(NoItemRecycleViewObserver(it, this))
        }

        if (linearLayoutHorizontal) {
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        //recyclerView.layoutManager = GridLayoutManager(context,Utility.calculateNoOfColumns(context))
        recyclerView.adapter = this
    }

    private val list = mutableMapOf<String, Item>()
    var onItemClick: ((Trace) -> Unit)? = null

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Item) {
            if (item.isDelete) {
                itemView.img_image.alpha = 0.5f
            }else{
                itemView.img_image.alpha = 1f
            }
            item.trace.apply {
                if (linearLayoutHorizontal) {
                    itemView.image_content.layoutParams.width = 200
                }

                itemView.img_image.setOnClickListener {
                    if (!item.isDelete) {
                        onItemClick?.invoke(this)
                    }
                }
                if (this.distance == null) {
                    itemView.layout_distance.visibility = View.INVISIBLE
                }
                if (this.distance != null && this.distance <= 0) {
                    itemView.layout_distance.visibility = View.INVISIBLE
                } else {
                    this.distance?.let {
                        if (this.distance <= 5000) {
                            itemView.tv_distance.text = "${String.format("%.1f", this.distance)}m"
                        } else {
                            itemView.tv_distance.text = ">1000m"
                        }
                    }

                }
                ImageUtil.loadImage(
                    context,
                    itemView.findViewById(R.id.img_image),
                    this.thumbnail,
                    ImageUtil.createCenterCropRequestOption().signature(ObjectKey(this.thumbnail)).override(200)
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(parent.inflate(R.layout.layout_item_image))

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(list.values.toList()[position])
    }

    fun add(image: Trace) {
        list.put(image.id, Item(image))
        notifyItemInserted(list.values.size - 1)
    }

    fun update(image: Trace) {
        list.put(image.id, Item(image))
        notifyDataSetChanged()
    }

    fun addAll(images: Array<Trace>) {
        list.putAll(images.map { return@map Pair(it.id, Item(it)) })
        notifyDataSetChanged()
    }

    fun remove(id: String) {
        val i = list.values.toList().indexOf(list.remove(id))
        notifyItemRemoved(i)
    }

    fun pendingRemove(id: String) {
        val item = list.get(id)
        val index = list.values.indexOf(item)
        item!!.isDelete = true
        notifyItemChanged(index)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun initLoadMore(func: (Int) -> Unit) {
        recyclerView.addOnScrollListener(object :
            GridEndlessRecyclerOnScrollListener(recyclerView.layoutManager as GridLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                func(currentPage)
            }

        })
    }

    fun getItems(): TraceList {
        val traceList = TraceList()
        traceList.addAll(this.list.values.map { return@map it.trace })
        return traceList
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

data class Item(val trace: Trace, var isDelete: Boolean = false)

class TraceList : ArrayList<Trace>() {
    override fun contains(element: Trace): Boolean {
        return find { trace -> element.id == trace.id } != null
    }
}