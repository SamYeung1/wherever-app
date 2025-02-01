package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
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
import com.samyeung.wherever.model.Comment
import kotlinx.android.synthetic.main.layout_list_comment.view.*
import com.samyeung.wherever.view.custom.ContextMenuRecyclerView


class CommentListAdapter(
    val context: Context,
    val recyclerView: ContextMenuRecyclerView,
    private val noItemView: View
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        this.registerAdapterDataObserver(NoItemRecycleViewObserver(noItemView, this))
        recyclerView.adapter = this
    }

    private val list = mutableListOf<Item>()
    var onItemUserIconClick: ((Comment) -> Unit)? = null

    inner class TraceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(i: Item) {
            if (i.isDelete || i.isUpdating) {
                itemView.findViewById<ConstraintLayout>(R.id.layout_main).alpha = 0.5f
            } else {
                itemView.findViewById<ConstraintLayout>(R.id.layout_main).alpha = 1f
            }
            i.comment.let { item ->
                var isTextViewClicked = false
                itemView.findViewById<ImageView>(R.id.img_primary_icon).setOnClickListener {
                    onItemUserIconClick?.invoke(item)
                }
                if (!i.isDelete) {
                    itemView.setOnLongClickListener {
                        recyclerView.openContextMenu(adapterPosition,item)
                        true
                    }
                    itemView.card_layout.setOnClickListener {
                        if (isTextViewClicked) {
                            itemView.tv_message.maxLines = 2
                            isTextViewClicked = false
                        } else {
                            itemView.tv_message.maxLines = Int.MAX_VALUE
                            isTextViewClicked = true
                        }

                    }
                    itemView.foreground = ContextCompat.getDrawable(context, R.drawable.bg_selectable_menu_1)
                    itemView.isFocusable = true
                    itemView.isClickable = true

                }
                itemView.tv_user.text = item.user.displayName
                itemView.tv_message.text = item.message
                itemView.findViewById<TextView>(R.id.tv_post_date).text =
                    if (i.isDelete) context.resources.getString(R.string.removing) else if (i.isUpdating)
                        context.resources.getString(R.string.updating) else
                        DateManager.createTimeDiff(context, item.postDate)
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
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TraceViewHolder(parent.inflate(R.layout.layout_list_comment))


    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TraceViewHolder) {
            holder.bind(list[position])
        }
    }


    fun add(comment: Comment) {
        list.add(Item(comment))
        notifyItemInserted(list.size - 1)
    }

    fun addToTop(comment: Comment) {
        list.add(0, Item(comment))
        notifyItemInserted(0)
    }

    fun moveToTop() {
        recyclerView.scrollToPosition(0)
    }

    fun update(comment: Comment) {
        val item = list.find { it.comment.id == comment.id }
        val index = list.indexOf(list.find { it.comment.id == comment.id })
        item!!.comment = comment
        list.set(index, item)
        item.isUpdating = false
        notifyItemChanged(index)
    }

    fun addAll(comments: Array<Comment>) {
        list.addAll(comments.map { return@map Item(it) }.filter { return@filter list.find { item -> item.comment.id == it.comment.id } == null })
        notifyDataSetChanged()
    }

    fun remove(id: String) {
        val item = list.find { it.comment.id == id }
        val index = list.indexOf(item)
        list.removeAt(index)
        notifyItemRemoved(index)
    }

    fun pendingRemove(id: String) {
        val item = list.find { it.comment.id == id }
        val index = list.indexOf(item)
        item!!.isDelete = true
        notifyItemChanged(index)
    }

    fun pendingUpdate(id: String) {
        val item = list.find { it.comment.id == id }
        val index = list.indexOf(item)
        item!!.isUpdating = true
        notifyItemChanged(index)
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

    fun getItems(): List<Comment> {
        return this.list.map { return@map it.comment }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

    data class Item(var comment: Comment, var isDelete: Boolean = false, var isUpdating: Boolean = false)
}