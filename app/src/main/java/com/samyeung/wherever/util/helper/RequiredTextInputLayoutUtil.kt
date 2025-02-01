package com.samyeung.wherever.util.helper

import android.content.Context
import android.support.design.widget.TextInputLayout
import com.samyeung.wherever.R

object RequiredTextInputLayoutUtil {
    fun setUpMarkRequired(context: Context,layout: TextInputLayout){
        layout.hint = "${layout.hint} ${context.resources.getString(R.string.asteriskred)}"
    }
}