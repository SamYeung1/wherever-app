package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.signature.ObjectKey
import com.samyeung.wherever.R
import com.samyeung.wherever.model.Home
import com.samyeung.wherever.util.NoItemRecycleViewObserver
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.util.helper.LinearEndlessRecyclerOnScrollListener
import com.samyeung.wherever.model.Request
import kotlinx.android.synthetic.main.layout_list_friend.view.tv_user
import kotlinx.android.synthetic.main.layout_list_request.view.*


const val FRIEND_TYPE = 0

class RequestListAdapter(
    val context: Context,
    val recyclerView: RecyclerView,
    private val noItemView:View
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        this.registerAdapterDataObserver(NoItemRecycleViewObserver(noItemView, this))
        recyclerView.adapter = this
    }

    private val list = mutableMapOf<String, Request>()
    var onItemClick: ((Request) -> Unit)? = null
    var onAcceptClick: ((Request) -> Unit)? = null
    var onCancelClick: ((Request) -> Unit)? = null

    inner class FriendRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Request) {
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
            itemView.btn_accept.setOnClickListener {
                onAcceptClick?.invoke(item)
                remove(item.id)
            }
            itemView.btn_cancel.setOnClickListener {
                onCancelClick?.invoke(item)
                remove(item.id)
            }
            itemView.foreground = ContextCompat.getDrawable(context, R.drawable.bg_selectable_menu_1)
            itemView.isFocusable = true
            itemView.isClickable = true
            itemView.tv_user.text = item.user.displayName
            item.let {
                ImageUtil.loadImage(
                    context,
                    itemView.findViewById(R.id.img_primary_icon),
                    it.user.icon,
                    ImageUtil.createCircleCropRequestOption().fallback(
                        R.drawable.ic_person_icon_default
                    ).signature(ObjectKey(it.user.icon ?: "")).override(200)
                )
            }

        }
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FriendRequestViewHolder) {
            holder.bind(list.values.toList()[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            FRIEND_TYPE -> {
                FriendRequestViewHolder(parent.inflate(R.layout.layout_list_request))
            }
            else -> {
                FriendRequestViewHolder(parent.inflate(R.layout.layout_list_request))
            }
        }

    override fun getItemViewType(position: Int): Int = when (list.toList()[position].second.type) {
        Request.TYPE_FRIEND -> FRIEND_TYPE
        else -> FRIEND_TYPE
    }

    fun add(request: Request) {
        list.put(request.id, request)
        notifyItemInserted(list.values.size - 1)
    }

    fun update(request: Request) {
        list.put(request.id, request)
        notifyDataSetChanged()
    }

    fun addAll(request: Array<Request>) {
        list.putAll(request.map { return@map Pair(it.id, it) })
        notifyDataSetChanged()
    }

    fun remove(id: String) {
        val i = list.values.toList().indexOf(list.remove(id))
        notifyItemRemoved(i)
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

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}