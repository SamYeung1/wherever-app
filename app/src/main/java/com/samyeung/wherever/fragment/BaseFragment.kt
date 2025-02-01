package com.samyeung.wherever.fragment

import android.os.Build
import android.os.Bundle
import android.support.transition.TransitionInflater
import android.support.v4.app.Fragment
import android.view.WindowManager
import com.samyeung.wherever.util.helper.ToolbarUtil
import kotlinx.android.synthetic.main.tool_bar.*

abstract class BaseFragment : Fragment() {
    var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        retainInstance = true
    }

    protected fun setUpStatusBarColor(color: Int) {
        activity?.window?.statusBarColor = color
    }

    protected fun setUpNavigationBar(color: Int) {
        activity!!.window.navigationBarColor = color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity!!.window.navigationBarDividerColor = color
        }
    }

    protected fun setUpToolbar(back: Boolean, isDark: Boolean = false) {
        toolbar?.let {
            ToolbarUtil.setUpToolbar(activity!!, it, title, back, isDark)
        }
    }

    open fun getSharedTransaction(): SharedTransaction? {
        return null
    }

    protected fun setUpSharedTransaction() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }
    open fun onBackPressed():Boolean{
        return false
    }

    class SharedTransaction(private val views: Array<Int>) {
        fun toMappedTransaction(): List<TransactionView> {
            return views.map { viewID ->
                TransactionView(
                    viewID
                )
            }
        }

        data class TransactionView(val id: Int)
    }
}