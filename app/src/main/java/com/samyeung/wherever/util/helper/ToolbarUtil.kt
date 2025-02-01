package com.samyeung.wherever.util.helper

import android.view.Gravity
import android.app.ActionBar
import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.samyeung.wherever.R


object ToolbarUtil {
    fun setUpToolbar(activity: Activity,toolbar:Toolbar,title:String,back:Boolean = false,isDark:Boolean = false){
        toolbar.let {
            if (activity is AppCompatActivity) {
                activity.setSupportActionBar(it)
                activity.supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
                activity.supportActionBar?.setCustomView(if(isDark) R.layout.toolbar_title_dark else R.layout.toolbar_title)
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(back)
                activity.supportActionBar?.setDisplayShowHomeEnabled(back)
            }

            var titleId = 0
            titleId = activity.resources.getIdentifier("action_bar_title", "id", activity.packageName)

            // Final check for non-zero invalid id
            if (titleId > 0) {
                val titleTextView = activity.findViewById(titleId) as? TextView
                titleTextView?.let {
                    val metrics = activity.resources.displayMetrics

                    // Fetch layout parameters of titleTextView (LinearLayout.LayoutParams : Info from HierarchyViewer)
                    val titleTxvPars = titleTextView.layoutParams as LinearLayout.LayoutParams
//                    titleTxvPars.gravity = Gravity.CENTER
//                    titleTxvPars.width = metrics.widthPixels

                    titleTextView.layoutParams = titleTxvPars
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.text = title

                }

            }
        }
    }
}