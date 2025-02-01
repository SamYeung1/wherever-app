package com.samyeung.wherever.fragment.editor.page.base


import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.Marker

import com.samyeung.wherever.R
import com.samyeung.wherever.view.adapter.map.GoogleMapAdapter
import com.samyeung.wherever.view.adapter.map.GoogleMapClusterRender
import com.samyeung.wherever.model.Location

abstract class LocationEditorFragment : BaseEditorFragment() {

    protected lateinit var data: Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            arguments?.let {
                this.data = it.getParcelable(KEY_DATA)!!
            }
        } else {
            savedInstanceState.let {
                this.data = it.getParcelable(KEY_DATA)!!
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_DATA, data)
    }
    fun getInputData(): Data = this.data

    protected fun setUpGoogleMap(googleMapAdapter:GoogleMapAdapter,onMarkerDrag:(Marker)->Unit){
        this.getInputData().location.let {
            googleMapAdapter.addMarker("original", GoogleMapClusterRender.Marker("original", null, "", it))
            googleMapAdapter.move(it.lat, it.lng, 17f)
            googleMapAdapter.onMarkerDrag = { marker ->
                onMarkerDrag(marker)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.bindView(savedInstanceState)
    }

    protected open fun bindView(savedInstanceState: Bundle?) {

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_editor, container, false)
    }

    data class Data(
        val imagePath: String,
        val location: Location,
        var readType: String = ""
    ):Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(imagePath)
            parcel.writeParcelable(location, flags)
            parcel.writeString(readType)
        }

        override fun describeContents(): Int {
            return 0
        }
        fun deepCopy():Data{
            return Data(this.imagePath,this.location.deepCopy(),this.readType)
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun canUpload(): Boolean {
        return true
    }

    override fun showError() {

    }
    companion object {
        const val KEY_DATA = "data"
        fun setUp(fragment: LocationEditorFragment, data: Data) {
            fragment.arguments = Bundle().apply {
                this.putParcelable(KEY_DATA, data)
            }
        }
    }
}
