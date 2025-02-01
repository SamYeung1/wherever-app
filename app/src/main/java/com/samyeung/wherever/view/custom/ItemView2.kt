package com.samyeung.wherever.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.samyeung.wherever.R
import kotlinx.android.synthetic.main.layout_item_2.view.*

class ItemView2 : FrameLayout {
    var title: String = ""
        set(value) {
            field = value
            item_view_tv_title.text = value
        }
    var value: String = ""
        set(value) {
            field = value
            item_view_tv_value.text = value
        }
    var icon: Drawable? = null
        set(value) {
            field = value
            item_view_img_icon_image.setImageDrawable(value)
        }
    var canEdit: Boolean = false
        set(value) {
            field = value
            image_view_img_edit.visibility = if (canEdit) View.VISIBLE else View.INVISIBLE
        }
    var allCap: Boolean = false
        set(value) {
            field = value
            item_view_tv_title.isAllCaps = value
        }
    var view: View? = null
        private set

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ItemView2, 0, 0)
        try {
            setUp()
            this.icon = a.getDrawable(R.styleable.ItemView2_itemView2Icon)
            this.title = a.getString(R.styleable.ItemView2_itemView2Title)!!
            this.canEdit = a.getBoolean(R.styleable.ItemView2_itemView2CanEdit, false)
            this.allCap = a.getBoolean(R.styleable.ItemView2_itemView2AllCap, true)
            a.getString(R.styleable.ItemView2_itemView2Value)?.let {
                this.value = it
            }
            // bind()
        } finally {
            a.recycle()
        }
    }

    private fun setUp() {
        this.view = inflate(context, R.layout.layout_item_2,this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bind()
    }

    private fun bind() {
        item_view_img_icon_image.setImageDrawable(icon)
        item_view_tv_title.text = title
        item_view_tv_value.text = value
        image_view_img_edit.visibility = if (canEdit) View.VISIBLE else View.INVISIBLE
        item_view_tv_title.isAllCaps = allCap
    }
}