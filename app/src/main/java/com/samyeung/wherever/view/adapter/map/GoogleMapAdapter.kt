package com.samyeung.wherever.view.adapter.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager

open class GoogleMapAdapter(private val context: Context,val map: GoogleMap) {
    protected open var enableGPS = true
    private val markers = mutableMapOf<String,GoogleMapClusterRender.Marker>()
    protected open val mClusterManager = ClusterManager<GoogleMapClusterRender.Marker>(context,map)
    var onMarkerClick:((GoogleMapClusterRender.Marker)->Unit)? = null
    var onMarkerDrag:((Marker)->Unit)? = null
    init {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED && enableGPS) {
            this.map.isMyLocationEnabled = true
        }
        this.map.uiSettings.isMyLocationButtonEnabled = false
        this.map.uiSettings.isCompassEnabled = false
        this.map.uiSettings.isZoomControlsEnabled = false
        this.map.uiSettings.isMapToolbarEnabled = false
        this.map.isTrafficEnabled = false
        this.map.setOnCameraIdleListener {
            mClusterManager.onCameraIdle()
        }
        this.map.setOnMarkerDragListener(object :GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragEnd(p0: Marker?) {
                this@GoogleMapAdapter.onMarkerDrag?.invoke(p0!!)
            }

            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {

            }

        })
        this.mClusterManager.setOnClusterItemClickListener {
            onMarkerClick?.invoke(it)
            return@setOnClusterItemClickListener true
        }
        this.map.setOnMarkerClickListener(mClusterManager)
        this.mClusterManager.renderer = GoogleMapClusterRender(context,map,this.mClusterManager)
    }
    fun addMarker(id:String,marker: GoogleMapClusterRender.Marker){
        if(this.markers.containsKey(id)){
            this.mClusterManager.removeItem(this.markers.get(id))
            this.markers.put(id, marker)
            this.mClusterManager.addItem(marker)
        }else {
            this.markers.put(id, marker)
            this.mClusterManager.addItem(marker)
        }
        this.mClusterManager.cluster()
    }
    fun removeMarker(id:String){
        this.mClusterManager.removeItem(this.markers.remove(id))
        this.mClusterManager.cluster()
    }
    fun getMarker(id:String):GoogleMapClusterRender.Marker{
        return this.markers.get(id)!!
    }
    fun clearMarker(){
        this.mClusterManager.clearItems()
        this.mClusterManager.cluster()
    }

    fun move(lat:Double,lng:Double,zoom:Float,animate:Boolean = false){
        if(animate) {
            this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
        }else{
            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
        }
    }
    fun drawCircle(lat: Double,lng: Double,radius:Double,backgroundColor:Int,strokeColor:Int): Circle {
        val circleOption = CircleOptions()
        circleOption.radius(radius)
        circleOption.center(LatLng(lat,lng))
        circleOption.fillColor(backgroundColor)
        circleOption.strokeColor(strokeColor)
        return this.map.addCircle(circleOption)
    }
}