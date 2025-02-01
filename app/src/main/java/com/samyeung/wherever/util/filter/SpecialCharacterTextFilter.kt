package com.samyeung.wherever.util.filter

import android.text.InputFilter
import android.text.Spanned
import java.io.Serializable

class SpecialCharacterTextFilter() :InputFilter, Serializable {
    override fun filter(p0: CharSequence?, p1: Int, p2: Int, p3: Spanned?, p4: Int, p5: Int): CharSequence? {
        //for (i in p1 until p2) {
            if (!p0!!.matches(Regex("^[^`~!@#\$%^&*()_+={}\\[\\]|\\\\:;“’<,>.?๐฿]*\$"))) {
                return ""
            }
       // }
        return null
    }
}