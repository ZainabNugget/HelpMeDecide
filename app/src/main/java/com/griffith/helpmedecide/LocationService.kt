package com.griffith.helpmedecide

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
class LocationService : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private val apiKey = R.string.places_api_key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(apiKey))
        }

        placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationTextView = findViewById<TextView>(R.id.location)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val center = LatLng(location.latitude, location.longitude)
                    val search_radius = CircularBounds.newInstance(center, 1000.0)
                    locationTextView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    getNearbyPlaces(location.latitude, location.longitude)
                } else { //if location unavailable
                    locationTextView.setText(R.string.location_info)
                }
            }
        } else { //If we don't have permission to access location!
            locationTextView.setText(R.string.location_unavailable)
        }
    }

    private fun getNearbyPlaces(latitude: Double, longitude: Double) {
        val placeFields = listOf(Place.Field.NAME)
        val placeResponse: Task<FindCurrentPlaceResponse>
        val request = FindCurrentPlaceRequest.newInstance(placeFields)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    //Iterate over the places found
                    response?.placeLikelihoods?.forEach { placeLikelihood ->
                        val placeName = placeLikelihood.place.name
                        val likelihood = placeLikelihood.likelihood
                        Log.i("NearbyPlaces", "Place: $placeName, Likelihood: $likelihood")
                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e("NearbyPlaces", "Error: ${exception.statusCode}")
                    }
                }
            }
        }
    }
}
