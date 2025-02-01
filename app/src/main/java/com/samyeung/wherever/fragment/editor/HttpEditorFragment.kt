package com.samyeung.wherever.fragment.editor

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import com.samyeung.wherever.R
import com.samyeung.wherever.api.ImageTraceService
import com.samyeung.wherever.api.ImageTraceServiceAdapter
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.model.Location
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.fragment.editor.page.base.BaseEditorPagerAdapter
import com.samyeung.wherever.fragment.editor.page.base.DescriptionEditorFragment
import com.samyeung.wherever.fragment.editor.page.base.LocationEditorFragment
import com.samyeung.wherever.fragment.editor.page.http.HttpDescriptionEditorFragment
import com.samyeung.wherever.fragment.editor.page.http.HttpLocationEditorFragment
import com.samyeung.wherever.fragment.main.GalleryFragment
import kotlinx.android.synthetic.main.fragment_editor.*

class HttpEditorFragment : EditorFragment() {
    private lateinit var traceId: String
    private lateinit var imageTraceService: ImageTraceServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.traceId = arguments!!.getString(TRACE_ID)
        this.imageTraceService = object : ImageTraceServiceAdapter(context!!) {
            override fun onUpdateTrace(trace: Trace) {
                this@HttpEditorFragment.stopLoading()
                GalleryFragment.GalleryLiveData.get().setUpdatedTrace(trace)
                activity!!.finish()
            }
        }
        this.title = context!!.resources.getString(R.string.edit_image)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager_editor.adapter = PagerAdapterForHttp(childFragmentManager, this.descriptionData, this.locationData)
    }

    override fun done() {
        val pagerAdapter = pager_editor.adapter!! as PagerAdapterForHttp
        val imageE =
            (pagerAdapter.getItem(0) as DescriptionEditorFragment).getDataForPrepareUpload() as DescriptionEditorFragment.Data
        val locationE =
            (pagerAdapter.getItem(1) as LocationEditorFragment).getDataForPrepareUpload() as LocationEditorFragment.Data
        this.imageTraceService.updateTrace(
            traceId, ImageTraceService.UpdateTraceBody(
                imageE.title, imageE.description, locationE.location.lng, locationE.location.lat,
                imageE.tags
            )
        )
        this.startLoading()
    }

    private class PagerAdapterForHttp(
        fragmentManager: FragmentManager,
        private val descriptionData: DescriptionEditorFragment.Data,
        private val locationData: LocationEditorFragment.Data
    ) :
        BaseEditorPagerAdapter(fragmentManager) {
        private val fragments = arrayOf(
            HttpDescriptionEditorFragment.newInstance(descriptionData),
            HttpLocationEditorFragment.newInstance(locationData)
        )

        override fun getItem(p0: Int): Fragment = fragments[p0]

        override fun getCount(): Int = fragments.size

    }

    private fun startLoading() {
        LoadingDialog.show(childFragmentManager, "")
    }

    private fun stopLoading() {
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(
            this,
            LoadingDialog.FRA_TAG
        ) as? LoadingDialog)?.dismissAllowingStateLoss()
    }

    companion object {
        const val TRACE_ID: String = "trace_id"
        fun start(
            activity: Activity,
            trace: Trace
        ) {
            val bundle = Bundle()
            val descriptionData = DescriptionEditorFragment.Data(trace.banners[0], trace.title,trace.description,trace.tags)
            descriptionData.readType = READ_TYPE_HTTP
            val locationData = LocationEditorFragment.Data(trace.banners[0], Location(trace.latitude, trace.longitude))
            locationData.readType = READ_TYPE_HTTP
            bundle.putParcelable(DESCRIPTION_DATA, descriptionData)
            bundle.putParcelable(LOCATION_DATA, locationData)
            bundle.putString(READ_TYPE, READ_TYPE_FILE)
            bundle.putString(TRACE_ID, trace.id)
            PortraitBaseActivity.start(activity, HttpEditorFragment(), bundle)
        }
    }
}