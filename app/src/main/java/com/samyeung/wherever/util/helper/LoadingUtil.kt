package com.samyeung.wherever.util.helper

import android.support.constraint.ConstraintLayout
import android.view.View
import com.samyeung.wherever.R

object LoadingUtil {
    fun show(contentView: View) {
        contentView.findViewById<ConstraintLayout>(R.id.loading_indicator)?.visibility = View.VISIBLE
    }

    fun hide(contentView: View) {
        contentView.visibility = View.VISIBLE
        contentView.findViewById<ConstraintLayout>(R.id.loading_indicator)?.visibility = View.INVISIBLE
    }
}