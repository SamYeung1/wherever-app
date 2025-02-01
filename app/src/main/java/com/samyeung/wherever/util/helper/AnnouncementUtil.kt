package com.samyeung.wherever.util.helper

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.layout_announcement.view.*

object AnnouncementUtil {
    fun setUp(context: Context,view: View, message: String, onViewButtonClick: (view: View) -> Unit) {
//        val sharedPreferences = context.getSharedPreferences("static", Context.MODE_PRIVATE)
//        if(!sharedPreferences.getBoolean("clicked_announcement",false) && canHide){
//            view.visibility = View.GONE
//            return
//        }else{
           view.visibility = View.VISIBLE
//        }
        view.tv_msg.text = message
        view.btn_view.setOnClickListener { onViewButtonClick(it) }
        view.btn_close.setOnClickListener {
//            val edit = sharedPreferences.edit()
//            edit.putBoolean("clicked_announcement",true)
//            edit.apply()
            view.visibility = View.GONE
        }
    }
}