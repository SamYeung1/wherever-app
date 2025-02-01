package com.samyeung.wherever.fragment.editor.page.file


import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.view.adapter.map.GoogleMapAdapter
import com.samyeung.wherever.view.adapter.map.GoogleMapAdapterForEditor
import com.samyeung.wherever.view.adapter.map.GoogleMapClusterRender
import com.samyeung.wherever.fragment.editor.page.base.LocationEditorFragment
import kotlinx.android.synthetic.main.fragment_location_editor.*

class FileLocationEditorFragment : LocationEditorFragment(), OnMapReadyCallback {
    private lateinit var googleMapAdapter: GoogleMapAdapter
    private lateinit var originalData: Data
    private lateinit var outputData: Data
    override fun onMapReady(p0: GoogleMap?) {
        this.googleMapAdapter = GoogleMapAdapterForEditor(context!!, p0!!)
        setUpGoogleMap(this.googleMapAdapter){marker->
            if (marker.title == "original") {
                this.outputData.location.lat = marker.position.latitude
                this.outputData.location.lng = marker.position.longitude
            }
            this.showRefresh()
        }
    }

    override fun getDataForPrepareUpload(): Data {
        return outputData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.originalData = getInputData().deepCopy()
        this.outputData = getInputData()
    }

    override fun bindView(savedInstanceState: Bundle?) {
        this.setUpMap()
        this.showRefresh()
        fab_refresh.setOnClickListener {
            this.outputData = originalData.deepCopy()
            this.showRefresh()
            this.googleMapAdapter.addMarker(
                "original",
                GoogleMapClusterRender.Marker("original", null, "", this.originalData.location)
            )
        }
    }

    private fun showRefresh() {
        if (
            (this.originalData.location.lat == this.outputData.location.lat && this.originalData.location.lng == this.outputData.location.lng)
        ) {
            fab_refresh.hide()
        } else {
            fab_refresh.show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(data: Data) =
            FileLocationEditorFragment().apply {
                setUp(this, data)
            }
    }

    private fun setUpMap() {
        val childFragmentManager =
            FragmentUtil.getFragmentFromChildFragmentManagerById(this, R.id.map) as? SupportMapFragment
        childFragmentManager!!.getMapAsync(this)
    }
}
