package com.samyeung.wherever.util.helper

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


object KeyboardUtil {
    fun hideKeyboard(activity: Activity,view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}