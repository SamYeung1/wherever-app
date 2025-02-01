package com.samyeung.wherever.util.helper

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

object FragmentUtil {
    fun setFragment(
        savedInstanceState: Bundle?,
        activity: FragmentActivity, @IdRes content: Int,
        fragment: Fragment,
        tag: String,
        allowingStateLoss: Boolean = false
    ) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.replace(content, fragment, tag)
        } else {
            transaction.replace(
                content, getFragmentFromFragmentManagerByTag(
                    activity,
                    tag
                )!!, tag
            )
        }
        if (allowingStateLoss) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }

    }

    fun setFragment(mainFragment: Fragment, @IdRes content: Int, childFragment: Fragment, tag: String) {
        val transaction = mainFragment.childFragmentManager.beginTransaction()
        transaction.replace(content, childFragment, tag)
        transaction.commit()
    }

    fun getFragmentFromChildFragmentManagerByTag(mainFragment: Fragment, tag: String): Fragment? {
        val fragmentManager = mainFragment.childFragmentManager
        return fragmentManager.findFragmentByTag(tag)
    }

    fun getFragmentFromChildFragmentManagerById(mainFragment: Fragment, id: Int): Fragment? {
        val fragmentManager = mainFragment.childFragmentManager
        return fragmentManager.findFragmentById(id)
    }

    fun getFragmentFromFragmentManagerByTag(activity: FragmentActivity, tag: String): Fragment? {
        val fragmentManager = activity.supportFragmentManager
        return fragmentManager.findFragmentByTag(tag)
    }

    fun getFragmentFromFragmentManagerById(activity: FragmentActivity, id: Int): Fragment? {
        val fragmentManager = activity.supportFragmentManager
        return fragmentManager.findFragmentById(id)
    }

}