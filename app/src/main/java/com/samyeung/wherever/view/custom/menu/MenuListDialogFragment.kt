package com.samyeung.wherever.view.custom.menu

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.samyeung.wherever.R;
import com.samyeung.wherever.fragment.BaseBottomSheetDialogFragment
import com.samyeung.wherever.view.custom.ItemView3
import kotlinx.android.synthetic.main.fragment_menu_list_dialog.*
import kotlinx.android.synthetic.main.fragment_menu_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_ITEM_LIST = "item_list"
const val ARG_ITEM_TAG = "item_tag"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    MenuListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [MenuListDialogFragment.Listener].
 */
class MenuListDialogFragment : BaseBottomSheetDialogFragment() {
    private var data:Bundle? = null
    private var mListener: Listener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.data = arguments!!.getBundle(ARG_DATA)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = MenuAdapter(
            arguments!!.getParcelableArrayList<MenuItem>(ARG_ITEM_LIST),
            arguments!!.getString(ARG_ITEM_TAG)
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onMenuClicked(mTag: String, position: Int,data:Bundle? = null)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup, val mTag: String) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_menu_list_dialog_item, parent, false)) {

        internal val item: ItemView3 = itemView.iv_setting

        init {
            itemView.isClickable = true
            itemView.isFocusable = true
            itemView.foreground = ContextCompat.getDrawable(context!!, R.drawable.bg_selectable_menu_2)
            itemView.setOnClickListener {
                mListener?.let {
                    it.onMenuClicked(this.mTag, adapterPosition,this@MenuListDialogFragment.data)
                    dismiss()
                }
            }
        }
    }

    private inner class MenuAdapter internal constructor(
        private val items: ArrayList<MenuItem>,
        private val mTag: String
    ) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent, mTag)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val textView = holder.item.view!!.findViewById<TextView>(R.id.item_view_tv_title)
            val iconView = holder.item.view!!.findViewById<ImageView>(R.id.item_view_img_icon_image)
            holder.item.title = items[position].title
            if (items[position].color == null) {
                iconView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.colorPrimary))
                textView.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            } else {
                iconView.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, items[position].color!!))
                textView.setTextColor(ContextCompat.getColor(context!!, items[position].color!!))
            }

            textView.setTypeface(null, Typeface.DEFAULT.style)
            holder.item.icon = ContextCompat.getDrawable(context!!, items[position].icon)
        }

        override fun getItemCount(): Int {
            return this.items.size
        }
    }

    companion object {
        private const val ARG_DATA = "arg_data"
        // TODO: Customize parameters
        private fun newInstance(tag: String, items: ArrayList<MenuItem>, data: Bundle?): MenuListDialogFragment =
            MenuListDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_TAG, tag)
                    putParcelableArrayList(ARG_ITEM_LIST, items)
                    putBundle(ARG_DATA,data)
                }
            }

        fun show(fragmentManager: FragmentManager, items: ArrayList<MenuItem>, menuTag: String, tag:String = "menu", data: Bundle? = null) {
            newInstance(menuTag, items, data)
                .show(fragmentManager, tag)
        }
    }
}
