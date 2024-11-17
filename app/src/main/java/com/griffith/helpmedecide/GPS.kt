package com.griffith.helpmedecide

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/*
* https://developer.android.com/develop/sensors-and-location/location/permissions#request-location-access-runtime
* Gonna use this for reference
*
* https://developer.android.com/develop/sensors-and-location/location/request-updates
*
* Using app compat activity, i created the xml for it as well
* a little formatting to help me view it better :3
* */

class GPS : AppCompatActivity() {

    private lateinit var shareLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        shareLocationButton = findViewById(R.id.share_location_button)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    getCurrentLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                    getCurrentLocation()
                }
                else -> {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        shareLocationButton.setOnClickListener {
            if (checkLocationPermission()) {
                getCurrentLocation()
            } else {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            try {
                val fusedLocationClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this)

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Toast.makeText(this, "Location: $latitude, $longitude", Toast.LENGTH_SHORT)
                            .show()
                        shareLocation(latitude, longitude)
                    } else {
                        Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error fetching location: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(this, "Location permission is not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please grant location permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareLocation(latitude: Double, longitude: Double) {
        val locationMessage = "My current location: https://maps.google.com/?q=$latitude,$longitude"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, locationMessage)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share location via"))
    }
}
