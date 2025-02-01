package com.samyeung.wherever.util.validator

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.ValidationUtil

class EmptyValidator(private val context: Context) : ValidationUtil.Validate {
    override fun validate(text: Editable) {
        if (TextUtils.isEmpty(text.trim())) {
            throw EmptyException(context)
        }
    }

    class EmptyException(private val context: Context) :
        Exception(context.resources.getString(R.string.err_input_empty))
}