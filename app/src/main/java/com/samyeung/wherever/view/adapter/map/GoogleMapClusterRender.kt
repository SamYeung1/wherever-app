package com.samyeung.wherever.view.adapter.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.samyeung.wherever.R
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.model.Location
import com.samyeung.wherever.fragment.main.TraceListFragment


open class GoogleMapClusterRender(
    private val context: Context,
    private val map: GoogleMap,
    clusterManager: ClusterManager<Marker>
) : DefaultClusterRenderer<GoogleMapClusterRender.Marker>(context, map, clusterManager),
    ClusterManager.OnClusterClickListener<GoogleMapClusterRender.Marker> {
    init {
        clusterManager.setOnClusterClickListener(this)
        map.setOnMarkerClickListener(clusterManager)
        map.setOnInfoWindowClickListener(clusterManager)
    }

    private val view = LayoutInflater.from(context).inflate(R.layout.map_marker, null)

    private fun getMarkerBitmapFromView(view: View, bitmap: Bitmap, personIcon: Bitmap?, item: Trace?): Bitmap {
        view.findViewById<ImageView>(R.id.img_primary_icon).setImageBitmap(bitmap)
        if (item != null) {
            view.findViewById<ImageView>(R.id.img_star).visibility =
                if (item.isFavourite!!) View.VISIBLE else View.INVISIBLE
            if (personIcon != null) {
                view.findViewById<ImageView>(R.id.img_person).setImageBitmap(personIcon)
            } else {
                view.findViewById<ImageView>(R.id.img_person).setImageResource(R.drawable.ic_person_icon_default)
            }

        }

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = view.background
        if (drawable != null)
            drawable.draw(canvas)
        view.draw(canvas)
        view.destroyDrawingCache()
        return returnedBitmap
    }

    override fun onBeforeClusterItemRendered(item: Marker?, markerOptions: MarkerOptions?) {
        markerOptions?.icon(
            BitmapDescriptorFactory.fromBitmap(
                getMarkerBitmapFromView(
                    view,
                    item!!.getIcon()!!,
                    item.getPersonIcon(),
                    item.getImage()
                )
            )
        )
    }

    override fun shouldRenderAsCluster(cluster: Cluster<Marker>?): Boolean {
        cluster?.let {
            return it.size >= 2
        }
        return false
    }

    override fun onClusterClick(p0: Cluster<Marker>?): Boolean {
        val builder = LatLngBounds.builder()
        p0!!.items.forEach {
            builder.include(it.position)
        }
        val bounds = builder.build()
        try {
            if (this.map.cameraPosition.zoom > 20) {
                TraceListFragment.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    p0.items.map { it.getImage() }.toList() as ArrayList<Trace>
                )
            } else {
                this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }


    class Marker(
        private val title: String,
        private val icon: Bitmap?,
        private val snippet: String,
        private val position: Location,
        private val image: Trace? = null,
        private val iconPerson: Bitmap? = null
    ) :
        ClusterItem {
        override fun getSnippet(): String {
            return snippet
        }

        override fun getTitle(): String {
            return title
        }

        override fun getPosition(): LatLng {
            return LatLng(position.lat, position.lng)
        }

        fun getIcon(): Bitmap? {
            return icon
        }

        fun getPersonIcon(): Bitmap? {
            return iconPerson
        }

        fun getImage(): Trace? {
            return image
        }
    }
}