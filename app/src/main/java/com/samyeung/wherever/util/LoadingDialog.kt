package com.samyeung.wherever.util

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.samyeung.wherever.R

const val TITLE = "title"

class LoadingDialog : DialogFragment() {
    private var title: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        retainInstance = true
        arguments?.let {
            this.title = it.getString(TITLE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_loading, null)
        if(!TextUtils.isEmpty(this.title)){
            view.findViewById<TextView>(R.id.tv_title).text = this.title
            view.findViewById<TextView>(R.id.tv_title).visibility = View.VISIBLE
        }else{
            view.findViewById<TextView>(R.id.tv_title).visibility = View.GONE
        }
        val alertDialog = AlertDialog.Builder(activity!!, R.style.AppTheme_DialogLoading)
            .setView(view)
            .create()
        return alertDialog
    }

    companion object {
        val FRA_TAG = "loading"
        private fun newInstance(title: String): LoadingDialog {
            return LoadingDialog().apply {
                val bundle = Bundle()
                bundle.putString(TITLE, title)
                this.arguments = bundle
            }
        }

        fun show(fragmentManager: FragmentManager, title: String = "", tag: String = FRA_TAG) {
            val transaction = fragmentManager.beginTransaction()
            transaction.add(newInstance(title), tag)
            transaction.commitAllowingStateLoss()
        }

    }
}