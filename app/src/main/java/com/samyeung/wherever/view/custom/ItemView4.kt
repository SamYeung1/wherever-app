package com.samyeung.wherever.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.samyeung.wherever.R
import kotlinx.android.synthetic.main.layout_item_3.view.*

class ItemView4 : FrameLayout {
    var title: String = ""
        set(value) {
            field = value
            item_view_tv_title.text = value
        }
    var icon: Drawable? = null
        set(value) {
            field = value
            item_view_img_icon_image.setImageDrawable(value)
        }
    var view: View? = null
        private set

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ItemView4, 0, 0)
        try {
            setUp()
            this.icon = a.getDrawable(R.styleable.ItemView4_itemView4Icon)
            a.getString(R.styleable.ItemView4_itemView4Title)?.let {
                this.title = it
            }

            // bind()
        } finally {
            a.recycle()
        }
    }

    private fun setUp() {
        this.view = inflate(context, R.layout.layout_item_4,this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bind()
    }

    private fun bind() {
        item_view_img_icon_image.setImageDrawable(icon)
        item_view_tv_title.text = title
    }
}