package com.samyeung.wherever.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.samyeung.wherever.fragment.imageviewer.TraceImageViewFragment

class TraceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpFragment(TraceImageViewFragment(),savedInstanceState)

    }
    companion object {
        private val URL: String = "wherever://view_trace"
        fun start(activity: Activity, traceId: String, resultCode: Int? = null) {
            startFromURL(activity, parseURL(traceId),resultCode)
        }
        fun parseURL(traceId: String): Uri {
            return Uri.parse("$URL?trace_id=${traceId}")
        }
    }
}