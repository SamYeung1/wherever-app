package com.samyeung.wherever.util.validator

import android.content.Context
import android.text.Editable
import android.util.Patterns
import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.ValidationUtil

class EmailValidator(private val context: Context) : ValidationUtil.Validate {
    override fun validate(text: Editable) {
        if (!text.toString().matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            throw InvalidEmailException(context)
        }
    }

    class InvalidEmailException(private val context: Context) :
        Exception(context.resources.getString(R.string.err_input_validate_email))
}