package com.samyeung.wherever.util.validator

import android.content.Context
import android.text.Editable
import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.ValidationUtil

class LengthValidator(private val context: Context, private val length: Int) : ValidationUtil.Validate {
    override fun validate(text: Editable) {
        if (text.trim().length >= this.length) {
            throw LengthException(context)
        }
    }

    class LengthException(private val context: Context) :
        Exception(context.resources.getString(R.string.err_input_length))
}