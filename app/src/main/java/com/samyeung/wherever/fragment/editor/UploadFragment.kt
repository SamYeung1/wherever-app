package com.samyeung.wherever.fragment.editor


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.api.ImageTraceService
import com.samyeung.wherever.api.ImageTraceServiceAdapter
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.AlertUtils
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.fragment.page.ExploreTraceLiveData
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.layout_message_page_success.*

class UploadFragment : BaseFragment() {
    private lateinit var imageTrace: ImageTraceService.TraceBody
    private lateinit var imageTraceService: ImageTraceServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.imageTraceService = object : ImageTraceServiceAdapter(context!!) {
            override fun onInsertImage(trace: Trace?) {
                onFinish(trace!!)
            }

            override fun onInsertImageError() {
                this@UploadFragment.onError()
            }
        }
        arguments?.let {
            this.imageTrace = it.getParcelable(TRACE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        btn_done.setOnClickListener {
            activity!!.finish()
        }
        btn_error.setOnClickListener {
            onUpload()
            this@UploadFragment.doUploadTrace()
        }
        onUpload()
        prg_uploading.max = 100
        doUploadTrace()
    }

    private fun doUploadTrace() {
        imageTraceService.insertTrace(imageTrace) { processing ->
            prg_uploading.isIndeterminate = false
            prg_uploading.progress = processing.toInt()
            tv_progress_value.text = "${processing.toInt()} %"
        }
    }

    private fun onUpload() {
        tv_progress_value.text = context!!.resources.getString(R.string.uploading)
        prg_uploading.isIndeterminate = true
        layout_done.visibility = View.INVISIBLE
        layout_error.visibility = View.INVISIBLE
        layout_progressing.visibility = View.VISIBLE
    }

    private fun onFinish(trace: Trace) {
        ExploreTraceLiveData.get().setNumberOfLatestTrace(1)
        layout_done.visibility = View.VISIBLE
        layout_error.visibility = View.INVISIBLE
        layout_progressing.visibility = View.INVISIBLE
    }

    private fun onError() {
        layout_done.visibility = View.INVISIBLE
        layout_error.visibility = View.VISIBLE
        layout_progressing.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        this.imageTraceService.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        this.showAlertOnClose()
        return true
    }

    private fun showAlertOnClose() {
        AlertUtils.create(context!!, "", context!!.resources.getString(R.string.warm_discard_trace), {
        }, {
            activity!!.finish()
        }).show()
    }

    companion object {
        const val TRACE = "imageTrace"
        @JvmStatic
        fun newInstance() =
            UploadFragment().apply {
                arguments = Bundle().apply {

                }
            }

        fun start(activity: Activity, imageTrace: ImageTraceService.TraceBody) {
            val bundle = Bundle()
            bundle.putParcelable(TRACE, imageTrace)
            PortraitBaseActivity.start(activity, UploadFragment(), bundle)
        }
    }
}
