package com.samyeung.wherever.fragment.editor.page.base


import android.os.Bundle

import com.samyeung.wherever.fragment.BaseFragment

abstract class BaseEditorFragment : BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    abstract fun getDataForPrepareUpload(): Any
    abstract fun canUpload():Boolean
    abstract fun showError()
}
