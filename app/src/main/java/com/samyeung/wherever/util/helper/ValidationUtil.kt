package com.samyeung.wherever.util.helper

import android.support.design.widget.TextInputLayout
import android.text.Editable


object ValidationUtil {
    fun setError(textInputLayout: TextInputLayout,message:String){
        textInputLayout.isErrorEnabled = true
        textInputLayout.error = message
    }
    fun validateForTextInput(success: Success, validators: Array<TextInputValidator>){
        var result = 0
        validators.forEach {
            if(it.validate()){
                result++
            }
            if(result == validators.size){
                success.success()
            }

        }
    }
    class TextInputValidator(private val textInputLayout: TextInputLayout,private val validates: Array<Validate>){
        fun validate():Boolean {
            textInputLayout.isErrorEnabled = false
            try {
                validates.forEach {
                    it.validate(textInputLayout.editText?.text!!)
                }
            } catch (e: Exception) {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = e.message
                return false
            }
            return true
        }
    }

    interface Validate{
        fun validate(text: Editable)
    }

    interface Success {
        fun success()
    }
}