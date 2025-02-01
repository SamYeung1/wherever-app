package com.samyeung.wherever.util.helper

import android.content.Context
import android.support.annotation.IdRes
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import android.widget.FrameLayout
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.samyeung.wherever.R


object MenuUtil {
    fun updateBadge(menu: Menu, @IdRes id: Int, number: Int) {
        val menuItem = menu.findItem(id)
        val rootView = menuItem.actionView as FrameLayout
        val countTextView = rootView.findViewById<TextView>(R.id.tv_badge)
        countTextView.text = number.toString()
        countTextView.visibility = if (number > 0) View.VISIBLE else View.INVISIBLE
        val icon = rootView.findViewById<ImageView>(R.id.img_icon)
        icon.setImageDrawable(menuItem.icon)
    }
    fun updateBadge(menuItem: MenuItem, number: Int) {
        val rootView = menuItem.actionView as FrameLayout
        val countTextView = rootView.findViewById<TextView>(R.id.tv_badge)
        countTextView.text = number.toString()
        countTextView.visibility = if (number > 0) View.VISIBLE else View.INVISIBLE
        val icon = rootView.findViewById<ImageView>(R.id.img_icon)
        icon.setImageDrawable(menuItem.icon)
    }
    fun setUpBadge(menu: Menu, @IdRes id: Int, number: Int,onItemClick:(MenuItem)->Unit) {
        val menuItem = menu.findItem(id)
        val rootView = menuItem.actionView as FrameLayout
        val countTextView = rootView.findViewById<TextView>(R.id.tv_badge)
        countTextView.text = number.toString()
        countTextView.visibility = if (number > 0) View.VISIBLE else View.INVISIBLE
        val icon = rootView.findViewById<ImageView>(R.id.img_icon)
        icon.setImageDrawable(menuItem.icon)
        rootView.setOnClickListener {
            onItemClick(menuItem)
        }
    }
    fun setUpBadgeForBottomNavigation(context:Context,bottomNavigationView: BottomNavigationView,@IdRes menuItemId:Int, number: Int) {
        val notificationsTab = bottomNavigationView.findViewById<BottomNavigationItemView>(menuItemId)
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.layout_badge_menu_item_nav_layout, notificationsTab, false)
        val countTextView = rootView.findViewById<TextView>(R.id.tv_badge)
        countTextView.text = number.toString()
        countTextView.visibility = if (number > 0) View.VISIBLE else View.INVISIBLE
        notificationsTab.addView(rootView)
    }
    fun updateBadge(context:Context, bottomNavigationView: BottomNavigationView, @IdRes menuItemId:Int, number: Int) {
        val notificationsTab = bottomNavigationView.findViewById<BottomNavigationItemView>(menuItemId)
        val countTextView = notificationsTab.findViewById<TextView>(R.id.tv_badge)
        countTextView.text = number.toString()
        countTextView.visibility = if (number > 0) View.VISIBLE else View.INVISIBLE
    }
}