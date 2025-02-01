package com.samyeung.wherever.view.adapter.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap

class GoogleMapAdapterForEditor(private val context: Context, map: GoogleMap,canDrag:Boolean = false) : GoogleMapAdapter(context, map) {
    override var enableGPS: Boolean = false
    init {
        this.mClusterManager.renderer = OriginalGoogleMapClusterRender(context, map, this.mClusterManager)
        this.map.uiSettings.isZoomGesturesEnabled = canDrag
        this.map.uiSettings.isScrollGesturesEnabled = canDrag
        this.map.uiSettings.isRotateGesturesEnabled = canDrag
        this.map.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = canDrag
        this.map.uiSettings.isIndoorLevelPickerEnabled = false
        this.map.uiSettings.isTiltGesturesEnabled = false
    }
}