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
import kotlinx.android.synthetic.main.dialog_text_editor.view.*

open class TextEditorDialogFragment : DialogFragment() {
    private var mListener: Listener? = null
    private var initText: String = ""
    private var changedText: String = ""
    private var title: String = ""
    private var isMessage: Boolean = false
    private var uid: String = ""
    private var isRequired: Boolean = false
    private var canCap: Boolean = true
    private var limit: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        retainInstance = true
        arguments?.let {
            this.initText = it.getString(INIT_TEXT)
            this.title = it.getString(TITLE)
            this.isMessage = it.getBoolean(IS_MESSAGE)
            this.uid = it.getString(TAG)
            this.isRequired = it.getBoolean(IS_REQUIRED)
            this.canCap = it.getBoolean(CAN_CAP)
            this.limit = it.getInt(LIMIT)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_text_editor, null)
        view.et_txt.setText(initText)
        if (this.limit != -1) {
            view.etl_txt.counterMaxLength = this.limit
        }
        this.changedText = view.et_txt.text.toString().trim()
        view.et_txt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                this@TextEditorDialogFragment.changedText = p0!!.toString().trim()
                (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
                    if (isRequired)
                        (!TextUtils.isEmpty(p0.toString().trim())) && p0.toString().trim().length <= limit || (!TextUtils.isEmpty(
                            p0.toString().trim()
                        ) && limit == -1)
                    else
                        p0.toString().trim().length <= limit || limit == -1
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        view.et_txt.filters = getTextFilter()
        if (isMessage) {
            view.et_txt.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        } else {
            if (canCap) {
                view.et_txt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            } else {
                view.et_txt.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
        return AlertDialog.Builder(activity!!, R.style.AppTheme_Dialog)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                if (initText != changedText) {
                    this@TextEditorDialogFragment.mListener?.onDone(uid, view.et_txt.text.toString().trim())
                }

            }
            .setNegativeButton(
                android.R.string.cancel
            ) { _, _ ->
                if (initText != changedText) {
                    this@TextEditorDialogFragment.mListener?.onCancel(uid, view.et_txt.text.toString().trim())
                }
                dismiss()
            }
            .create()
    }

    override fun onAttach(context: Context?) {
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

    open fun getTextFilter(): Array<InputFilter> {
        return arrayOf(object : InputFilter {
            override fun filter(p0: CharSequence?, p1: Int, p2: Int, p3: Spanned?, p4: Int, p5: Int): CharSequence? {
                if (p0!!.contains("$")) {
                    return ""
                }
                return null
            }

        })
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
        ): TextEditorDialogFragment {
            return TextEditorDialogFragment().apply {
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