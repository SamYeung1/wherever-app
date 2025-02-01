package com.samyeung.wherever.view.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.samyeung.wherever.R
import kotlinx.android.synthetic.main.layout_message_box.view.*

class CommentBoxView : FrameLayout, TextWatcher {
    var message: String = ""
        set(value) {
            field = value
            et_comment.setText(value)
        }
    var view: View? = null
        private set

    private var sendButtonClick: SendButtonClick? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CommentBoxView, 0, 0)
        try {
            setUp()
            a.getString(R.styleable.CommentBoxView_commentBoxViewMessage)?.let {
                this.message = it
            }
            bind()
        } finally {
            a.recycle()
        }
    }

    private fun setUp() {
        this.view = inflate(context, R.layout.layout_message_box, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bind()
    }

    private fun bind() {
        fab_post.hide()
        et_comment.setText(message)
        et_comment.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty() || s.isNullOrBlank()) {
            fab_post.hide()
            fab_post.setOnClickListener {

            }
        } else {
            fab_post.show()
            fab_post.setOnClickListener {
                this.sendButtonClick?.onSendButtonClick(it, et_comment.text.toString().trim())
                et_comment.setText("")
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    fun setSendButtonClick(sendButtonClick: SendButtonClick) {
        this.sendButtonClick = sendButtonClick
    }

    interface SendButtonClick {
        fun onSendButtonClick(view: View, message: String)
    }
}