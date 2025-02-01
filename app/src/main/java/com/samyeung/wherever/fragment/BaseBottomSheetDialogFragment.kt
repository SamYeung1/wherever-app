package com.samyeung.wherever.fragment

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View
import com.samyeung.wherever.R

open class BaseBottomSheetDialogFragment: BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun getTheme(): Int = R.style.AppTheme_BottomSheetDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(),theme)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            assert(view != null)
            val parent = view!!.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin),
                0,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_margin),
                0
            )
            parent.layoutParams = layoutParams
        }
    }
}