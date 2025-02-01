package com.samyeung.wherever.util.helper

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.support.annotation.MainThread
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.samyeung.wherever.cst.Setting
import com.samyeung.wherever.model.Location
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import io.nlopez.smartlocation.rx.ObservableFactory
import io.nlopez.smartlocation.utils.GooglePlayServicesListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


private lateinit var instance: MutableLiveData<Location>

object LocationUtil {
    private const val LOCATION_SERVICES_RESOLUTION_REQUEST = 1001
    private const val PLAY_SERVICES_RESOLUTION_REQUEST = 1000
    private const val INTERVAL: Long = Setting.GPS_INTERVAL
    private const val DISTANCE = Setting.GPS_DISTANCE
    private var locationObservable: Disposable? = null
    private var locationParameter = LocationParams.Builder()
        .setAccuracy(LocationAccuracy.HIGH)
        .setInterval(INTERVAL)
        .setDistance(DISTANCE)
        .build()
    private var mLocationRequest: LocationRequest? = null
    fun start(context: Context, onLocationUpdatedListener: OnLocationUpdatedListener) {
        locationObservable =
            ObservableFactory.from(SmartLocation.with(context).location(LocationGooglePlayServicesProvider(object :
                GooglePlayServicesListener {
                override fun onConnected(p0: Bundle?) {
                    showRequestDialog(context)
                }

                override fun onConnectionSuspended(p0: Int) {

                }

                override fun onConnectionFailed(p0: ConnectionResult?) {
                    Log.i("Connection Failed", "Connection failed: ConnectionResult.getErrorCode() = ${p0?.errorCode}")
                }

            })).continuous().config(locationParameter))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        onLocationUpdatedListener.onLocationUpdated(result)
                    }
                )
    }

    private fun showRequestDialog(context: Context) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.let {
            it.interval = INTERVAL
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        context.let {
            val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)
                .setAlwaysShow(true).build()

            val client = LocationServices.getSettingsClient(it)
            val task = client.checkLocationSettings(locationSettingsRequest)
            task.addOnFailureListener {
                val status = (it as ApiException)
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = it as ResolvableApiException
                            if (context is Activity) {
                                resolvable.startResolutionForResult(context, LOCATION_SERVICES_RESOLUTION_REQUEST)
                            }
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error
                        }
                    }
                }
            }
        }


    }

    private fun checkPlayServices(context: Context): Boolean {
        val resultCode = GooglePlayServicesUtil
            .isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                if (context is Activity) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST).show()
                }
            } else {

            }
            return false
        }
        return true
    }


    fun handleOnActivityResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        onLocationUpdatedListener: OnLocationUpdatedListener
    ): Boolean {
        var result = false
        if (requestCode == LOCATION_SERVICES_RESOLUTION_REQUEST)
            if (resultCode == Activity.RESULT_CANCELED) {
                showRequestDialog(context)
            } else {
                start(context, onLocationUpdatedListener)

            }
        result = true
        return result
    }

    fun onDestroy() {
        this.locationObservable?.dispose()
    }


    class LocationHolder : MutableLiveData<Location>() {
        companion object {

            @MainThread
            fun get(): MutableLiveData<Location> {
                instance = if (::instance.isInitialized) instance else LocationHolder()
                return instance
            }

            @MainThread
            fun setUp(lat: Double, lng: Double) {
                instance.value = Location(lat, lng)
            }
        }
    }
}