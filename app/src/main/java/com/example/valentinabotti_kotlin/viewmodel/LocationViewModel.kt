package com.example.valentinabotti_kotlin.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.valentinabotti_kotlin.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {

    private val _currentLocation = MutableStateFlow(Location(0f, 0f)) // Default location (Los Angeles)
    val currentLocation: StateFlow<Location> = _currentLocation

    private val _hasPermission = MutableLiveData(false)
    val hasPermission: LiveData<Boolean> = _hasPermission

    // Funzione per controllare i permessi
    fun checkLocalPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Funzione per gestire la richiesta di permessi
    fun requestPermission(context: Context, permissionLauncher: ActivityResultLauncher<String>) {
        if (!checkLocalPermission(context)) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            _hasPermission.value = true
        }
    }

    // Funzione per recuperare la posizione
    fun retrieveLocation(context: Context) {
        Log.d("LocationViewModel", "Retrieving location")
        if (checkLocalPermission(context)) {
            retrieveuserLocation(context) { locationString ->
                locationString?.let {
                    val parts = it.split(",")
                    _currentLocation.value = Location(parts[0].toFloat(), parts[1].toFloat())
                }
                Log.d("LocationViewModel", "Location: $locationString")
                _hasPermission.value = true
            }
        } else {
            _currentLocation.value = Location(34f, -118f)
        }
    }

    // Funzione per gestire il risultato della richiesta di permesso
    fun onPermissionResult(isGranted: Boolean, context: Context) {
        _hasPermission.value = isGranted
        if (isGranted) {
            retrieveLocation(context)
        } else {
            _currentLocation.value = Location(34f, -118f)
        }
    }
}