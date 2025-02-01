package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.signature.ObjectKey
import com.samyeung.wherever.R
import com.samyeung.wherever.util.NoItemRecycleViewObserver
import com.samyeung.wherever.util.helper.DateManager
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.util.helper.LinearEndlessRecyclerOnScrollListener
import com.samyeung.wherever.model.Reaction
import kotlinx.android.synthetic.main.layout_list_user_reaction.view.*


class UserReactionListAdapter(
    val context: Context,
    val recyclerView: RecyclerView,
    private val noItemView:View
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        this.registerAdapterDataObserver(NoItemRecycleViewObserver(noItemView, this))
        recyclerView.adapter = this
    }

    private val list = mutableMapOf<String, Reaction>()
    var onItemClick: ((Reaction) -> Unit)? = null

    inner class UserReactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Reaction) {
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
            itemView.foreground = ContextCompat.getDrawable(context, R.drawable.bg_selectable_menu_1)
            itemView.isFocusable = true
            itemView.isClickable = true
            itemView.tv_user.text = item.user.displayName
            itemView.findViewById<TextView>(R.id.tv_post_date).text = DateManager.createTimeDiff(context, item.postDate)
            item.user.let {
                ImageUtil.loadImage(
                    context,
                    itemView.findViewById(R.id.img_primary_icon),
                    it.icon,
                    ImageUtil.createCircleCropRequestOption().fallback(
                        R.drawable.ic_person_icon_default
                    ).signature(ObjectKey(it.icon ?: "")).override(200)
                )
            }
            when (item.type) {
                Reaction.TYPE_LOVE -> {
                    itemView.findViewById<ImageView>(R.id.img_secondary_icon).setImageResource(R.drawable.ic_love_icon)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        UserReactionViewHolder(parent.inflate(R.layout.layout_list_user_reaction))


    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserReactionViewHolder) {
            holder.bind(list.values.toList()[position])
        }
    }


    fun add(reaction: Reaction) {
        list.put(reaction.user.id, reaction)
        notifyItemInserted(list.values.size - 1)
    }

    fun update(reaction: Reaction) {
        list.put(reaction.user.id, reaction)
        notifyDataSetChanged()
    }

    fun addAll(reaction: Array<Reaction>) {
        list.putAll(reaction.map { return@map Pair(it.user.id, it) })
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