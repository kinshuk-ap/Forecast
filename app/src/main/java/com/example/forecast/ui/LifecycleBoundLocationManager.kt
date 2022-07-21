package com.example.forecast.ui

import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority

class LifecycleBoundLocationManager(
    lifecycleOwner: LifecycleOwner,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationCallback: LocationCallback
): LifecycleObserver, DefaultLifecycleObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 5000
        priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
    }

//    fun startLocationUpdates() {
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
//    }
//    fun removeLoactionUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}