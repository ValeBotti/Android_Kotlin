package com.example.valentinabotti_kotlin.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.valentinabotti_kotlin.model.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {

    private val _currentLocation = MutableStateFlow(Location(0f, 0f))
    val currentLocation: StateFlow<Location> = _currentLocation

    private val _hasPermission = MutableLiveData<Boolean?>(null)
    val hasPermission: LiveData<Boolean?> = _hasPermission

    fun onPermissionResult(isGranted: Boolean) {
        Log.d("LocationVM", "onPermissionResult → $isGranted")
        _hasPermission.value = isGranted
    }

    fun checkLocalPermission(context: Context): Boolean {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("LocationVM", "checkLocalPermission → $granted")
        return granted
    }

    fun requestPermission(permissionLauncher: ActivityResultLauncher<String>) {
        Log.d("LocationVM", "requestPermission → launching permission request")
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun retrieveLocation(context: Context) {
        Log.d("LocationVM", "retrieveLocation → START")

        if (!checkLocalPermission(context)) {
            Log.d("LocationVM", "retrieveLocation → NO PERMISSION")
            return
        }

        retrieveuserLocation(context) { locationString ->
            Log.d("LocationVM", "retrieveLocation → callback = $locationString")

            if (locationString != null) {
                val parts = locationString.split(",")
                val lat = parts[0].toFloat()
                val lng = parts[1].toFloat()

                _currentLocation.value = Location(lat, lng)
                Log.d("LocationVM", "currentLocation UPDATED → ${_currentLocation.value}")
            } else {
                Log.e("LocationVM", "retrieveLocation → NULL from callback")
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun retrieveuserLocation(context: Context, onResult: (String?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        Log.d("LocationVM", "retrieveuserLocation → requesting single update")

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1500 // 1.5 secondi
        ).setMaxUpdates(1).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation
                    Log.d("LocationVM", "onLocationResult → $loc")

                    if (loc != null) {
                        val lat = loc.latitude
                        val lng = loc.longitude
                        Log.d("LocationVM", "NEW LOCATION → $lat,$lng")
                        onResult("$lat,$lng")
                    } else {
                        Log.e("LocationVM", "onLocationResult → NULL LOCATION")
                        onResult(null)
                    }

                    fusedLocationClient.removeLocationUpdates(this)
                    Log.d("LocationVM", "Location updates REMOVED")
                }
            },
            Looper.getMainLooper()
        )
    }
}
