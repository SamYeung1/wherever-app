package com.samyeung.wherever.view.adapter.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer


open class OriginalGoogleMapClusterRender(
    private val context: Context,
    private val map: GoogleMap,
    clusterManager: ClusterManager<GoogleMapClusterRender.Marker>,
    private val draggable:Boolean = true
) : DefaultClusterRenderer<GoogleMapClusterRender.Marker>(context, map, clusterManager),
    ClusterManager.OnClusterClickListener<GoogleMapClusterRender.Marker> {
    init {
        clusterManager.setOnClusterClickListener(this)
        map.setOnMarkerClickListener(clusterManager)
        map.setOnInfoWindowClickListener(clusterManager)
    }

    override fun onBeforeClusterItemRendered(item: GoogleMapClusterRender.Marker?, markerOptions: MarkerOptions?) {
        markerOptions?.draggable(draggable)
        markerOptions?.icon(
            BitmapDescriptorFactory.defaultMarker()
        )
    }

    override fun shouldRenderAsCluster(cluster: Cluster<GoogleMapClusterRender.Marker>?): Boolean {
        return false
    }

    override fun onClusterClick(p0: Cluster<GoogleMapClusterRender.Marker>?): Boolean {
        return false
    }

}