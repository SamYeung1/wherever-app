package com.samyeung.wherever.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v4.text.HtmlCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
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
import com.samyeung.wherever.model.UserInbox


const val TRACE_TYPE = 0

class UserInboxListAdapter(

    val context: Context,
    val recyclerView: RecyclerView,
    private val noItemView:View
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        this.registerAdapterDataObserver(NoItemRecycleViewObserver(noItemView, this))
        recyclerView.adapter = this
    }

    private val list = mutableMapOf<String, UserInbox>()
    var onItemClick: ((UserInbox) -> Unit)? = null

    inner class UserInboxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: UserInbox) {
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                ContextCompat.startActivity(context, intent, null)
            }
            itemView.findViewById<ImageView>(R.id.img_is_read).visibility =
                if (!item.isRead) View.VISIBLE else View.INVISIBLE
            itemView.foreground = ContextCompat.getDrawable(context, R.drawable.bg_selectable_menu_1)
            itemView.isFocusable = true
            itemView.isClickable = true
            itemView.findViewById<TextView>(R.id.tv_description).text =
                HtmlCompat.fromHtml(item.inboxContent, Html.FROM_HTML_MODE_COMPACT)
            itemView.findViewById<TextView>(R.id.tv_post_date).text = DateManager.createTimeDiff(context, item.postDate)
            item.user?.let {
                ImageUtil.loadImage(
                    context,
                    itemView.findViewById(R.id.img_secondary_icon),
                    it.icon,
                    ImageUtil.createCircleCropRequestOption().fallback(
                        R.drawable.ic_person_icon_default
                    ).signature(ObjectKey(it.icon ?: "")).override(200)
                )
            }
            if (item.type == UserInbox.TRACE || item.type == UserInbox.COMMENT) {
                if (item.trace != null) {
                    ImageUtil.loadImage(
                        context,
                        itemView.findViewById(R.id.img_primary_icon),
                        item.trace.thumbnail,
                        ImageUtil.createCircleCropRequestOption().signature(ObjectKey(item.trace.thumbnail)).override(200)
                    )
                } else {
                    item.icon?.let {
                        ImageUtil.loadImage(
                            context,
                            itemView.findViewById(R.id.img_primary_icon),
                            it,
                            ImageUtil.createCircleCropRequestOption().centerInside().signature(ObjectKey(it)).override(
                                200
                            )
                        )
                    }
                }

            } else {
                item.icon?.let {
                    ImageUtil.loadImage(
                        context,
                        itemView.findViewById(R.id.img_primary_icon),
                        it,
                        ImageUtil.createCircleCropRequestOption().centerInside().signature(ObjectKey(it)).override(200)
                    )
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TRACE_TYPE -> {
                UserInboxViewHolder(parent.inflate(R.layout.layout_list_user_inbox_trace))
            }
            else -> {
                UserInboxViewHolder(parent.inflate(R.layout.layout_list_user_inbox_trace))
            }
        }


    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserInboxViewHolder) {
            holder.bind(list.values.toList()[position])
        }
    }

    override fun getItemViewType(position: Int): Int = when (this.list.toList()[position].second.type) {
        UserInbox.COMMENT, UserInbox.TRACE -> {
            TRACE_TYPE
        }
        else -> {
            -1
        }
    }

    fun add(userInbox: UserInbox) {
        list.put(userInbox.id, userInbox)
        notifyItemInserted(list.values.size - 1)
    }

    fun update(userInbox: UserInbox) {
        list.put(userInbox.id, userInbox)
        notifyDataSetChanged()
    }

    fun addAll(userInbox: Array<UserInbox>) {
        list.putAll(userInbox.map { return@map Pair(it.id, it) })
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