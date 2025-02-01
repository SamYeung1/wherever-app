package com.samyeung.wherever.view.adapter.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap

class GoogleMapAdapterForView(private val context: Context, map: GoogleMap) : GoogleMapAdapter(context, map) {
    init {
        this.mClusterManager.renderer = OriginalGoogleMapClusterRender(context, map, this.mClusterManager,false)
        this.map.uiSettings.isZoomGesturesEnabled = false
        this.map.uiSettings.isScrollGesturesEnabled = false
        this.map.uiSettings.isRotateGesturesEnabled = false
        this.map.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
        this.map.uiSettings.isIndoorLevelPickerEnabled = false
        this.map.uiSettings.isTiltGesturesEnabled = false
    }
}