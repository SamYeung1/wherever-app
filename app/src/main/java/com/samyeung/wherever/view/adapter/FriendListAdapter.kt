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
import com.samyeung.wherever.api.RequestServiceAdapter
import com.samyeung.wherever.util.NoItemRecycleViewObserver
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.util.helper.LinearEndlessRecyclerOnScrollListener
import com.samyeung.wherever.view.custom.ContextMenuRecyclerView
import com.samyeung.wherever.model.UserProfile
import com.samyeung.wherever.util.helper.ButtonUtil
import kotlinx.android.synthetic.main.layout_list_friend.view.*


class FriendListAdapter(
    val context: Context,
    val recyclerView: RecyclerView,
    private val noItemView: View
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        this.registerAdapterDataObserver(NoItemRecycleViewObserver(noItemView, this))
        recyclerView.adapter = this
    }

    private val requestService: RequestServiceAdapter = RequestServiceAdapter(context)
    private val list = mutableMapOf<String, UserProfile>()
    var onItemClick: ((UserProfile) -> Unit)? = null

    inner class TraceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: UserProfile) {
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
            if (recyclerView is ContextMenuRecyclerView) {
                itemView.setOnLongClickListener {
                    recyclerView.openContextMenu(adapterPosition, item)
                    true
                }
            }
            itemView.foreground = ContextCompat.getDrawable(context, R.drawable.bg_selectable_menu_1)
            itemView.isFocusable = true
            itemView.isClickable = true
            itemView.tv_user.text = item.displayName
            itemView.btn_friend.visibility = View.INVISIBLE
            item.isMe?.let {
                if (it) {
                    itemView.btn_friend.visibility = View.INVISIBLE
                } else {
                    ButtonUtil.setUpFriendRelationshipButton(context, itemView.btn_friend, item, requestService)
                    itemView.btn_friend.visibility = View.VISIBLE
                }
            }

            item.let {
                ImageUtil.loadImage(
                    context,
                    itemView.findViewById(R.id.img_primary_icon),
                    it.icon,
                    ImageUtil.createCircleCropRequestOption().fallback(
                        R.drawable.ic_person_icon_default
                    ).signature(ObjectKey(it.icon ?: "")).override(200)
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TraceViewHolder(parent.inflate(R.layout.layout_list_friend))


    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TraceViewHolder) {
            holder.bind(list.values.toList()[position])
        }
    }


    fun add(user: UserProfile) {
        list.put(user.id, user)
        notifyItemInserted(list.values.size - 1)
    }

    fun update(user: UserProfile) {
        list.put(user.id, user)
        notifyDataSetChanged()
    }

    fun addAll(user: Array<UserProfile>) {
        list.putAll(user.map { return@map Pair(it.id, it) })
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

    fun getItems(): List<UserProfile> {
        return this.list.values.toList()
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