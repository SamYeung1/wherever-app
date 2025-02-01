package com.samyeung.wherever.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.*
import com.samyeung.wherever.R
import com.samyeung.wherever.util.filter.EmojiTextFilter
import kotlinx.android.synthetic.main.dialog_text_editor.view.*

open class TextEditorNoEmojiDialogFragment :TextEditorDialogFragment(){
    override fun getTextFilter(): Array<InputFilter> {
        val list = super.getTextFilter().toMutableList()
        list.add(EmojiTextFilter())
        return list.toTypedArray()
    }
    companion object {
        private const val TITLE = "title"
        private const val TAG = "tag"
        private const val INIT_TEXT = "init_text"
        private const val IS_MESSAGE = "is_message"
        private const val IS_REQUIRED = "is_required"
        private const val CAN_CAP = "can_cap"
        private const val LIMIT = "limit"
        private fun newInstance(
            title: String,
            isMessage: Boolean,
            tag: String,
            text: String,
            isRequired: Boolean,
            cap: Boolean,
            max: Int
        ): TextEditorNoEmojiDialogFragment {
            return TextEditorNoEmojiDialogFragment().apply {
                val bundle = Bundle()
                bundle.putString(TITLE, title)
                bundle.putString(INIT_TEXT, text)
                bundle.putBoolean(IS_MESSAGE, isMessage)
                bundle.putString(TAG, tag)
                bundle.putBoolean(IS_REQUIRED, isRequired)
                bundle.putBoolean(CAN_CAP, cap)
                bundle.putInt(LIMIT, max)
                this.arguments = bundle
            }
        }

        fun show(
            fragmentManager: FragmentManager,
            title: String,
            isMessage: Boolean,
            initText: String,
            isRequired: Boolean,
            tag: String,
            cap: Boolean = false,
            max: Int = -1
        ) {
            newInstance(
                title,
                isMessage,
                tag,
                initText,
                isRequired,
                cap,
                max
            ).show(fragmentManager, "input_text")
        }
    }

    interface Listener {
        fun onDone(mTag: String, changedText: String)
        fun onCancel(mTag: String, changedText: String)
    }
}