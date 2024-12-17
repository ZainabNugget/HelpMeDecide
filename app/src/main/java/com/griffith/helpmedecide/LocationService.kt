package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.material.bottomnavigation.BottomNavigationView

class LocationService : AppCompatActivity() {
    //helps get the location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //from google
    private lateinit var placesClient: PlacesClient
    private val restaurantList = mutableListOf<String>()
    private val tourismList = mutableListOf<String>()
    //this is api key for google places api
    private val apiKey = R.string.places_api_key
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //refer to the res/xml/activity_location_services file, the design is all there
        setContentView(R.layout.activity_location_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //set up the bottom bar navigation system
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { //go to homepage!
                    val intent = Intent(this@LocationService, HomePage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        //init places to use the apis
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(apiKey))
        }
        //this is needed to use the googles api, also uses context
        placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationTextView = findViewById<TextView>(R.id.location)
        //make sure permission is already gotten from user
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.i("Current Location", "$location")
                        val center = LatLng(location.latitude, location.longitude)
                        val search_radius = CircularBounds.newInstance(center, 1000.0)
                        getNearbyRestaurants(location.latitude, location.longitude, restaurantList, listOf("restaurant"))
                        getNearbyRestaurants(location.latitude, location.longitude, tourismList, listOf("tourist_attraction"))
                        locationTextView.text = findPlaceName(location.latitude, location.longitude)
                    } else { //if location unavailable
                        locationTextView.setText(R.string.location_info)
                    }
                        Log.d("Location", "location is found: $location")
                    }
                .addOnFailureListener { exception ->
                    Log.d("Location", "Oops location failed with exception: $exception")
                }
        } else { //If we don't have permission to access location!
            locationTextView.setText(R.string.location_unavailable)
        }

        val restaurantBtn : Button = findViewById(R.id.restaurantBtn)
        restaurantBtn.setOnClickListener {
            val intent = Intent(this, SpinTheWheel::class.java)
            intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(restaurantList))
            intent.putExtra("USER_GENERATED", true)
            startActivity(intent)
        }

        val tourismBtn : Button = findViewById(R.id.tourismBtn)
        tourismBtn.setOnClickListener {
            if(tourismList.isNotEmpty()){
                val intent = Intent(this, SpinTheWheel::class.java)
                intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(tourismList))
                intent.putExtra("USER_GENERATED", true)
                startActivity(intent)
            } else {

            }
        }

    }

    private fun getNearbyRestaurants(latitude: Double, longitude: Double, list: MutableList<String>, includedTypes:List<String> ) {
        // Check for permissions before making a request
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle case where permission is not granted
            Log.e("NearbyPlaces", "Location permission not granted")
            return
        }

        //Define place fields to include in the response
        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ID)
        val center = LatLng(latitude, longitude)
        val radius = 1000
        val circle = CircularBounds.newInstance(center, radius.toDouble())

        //Create a SearchNearbyRequest using the location, place fields, and types
        val searchNearbyRequest = SearchNearbyRequest.builder(circle, placeFields)
            .setIncludedTypes(includedTypes)
            .setMaxResultCount(10)
            .build()

        //call the Places API to search for nearby places
        placesClient.searchNearby(searchNearbyRequest)
            .addOnSuccessListener { response ->
                //Iterate through the places and log the results
                response?.places?.forEach { place ->
                    val placeName = place.name
                    val placeAddress = place.address
                    if (placeName != null) {
                        list.add(placeName)
                    }
                    Log.i("Nearby", "Yay $placeName")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NearbyRestaurants", "Error fetching places: ${exception.message}")
            }

    }

    private fun findPlaceName(lat: Double, lon: Double) : String {
        // Define the fields to retrieve
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS
        )

        //create a request for the current place
        val request = FindCurrentPlaceRequest.newInstance(placeFields)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //error checking
            Log.e("Place result", "Location permission not granted")
        }
        //Call findCurrentPlace which is in the API
        val placeResult: Task<FindCurrentPlaceResponse> =
            placesClient.findCurrentPlace(request)
        var name = ""
        placeResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result
                for (placeLikelihood in response.placeLikelihoods) {
                    val place = placeLikelihood.place
                    val locationTextView = findViewById<TextView>(R.id.location)
                    locationTextView.text = place.name
                    //getting location when we havent fetched it yet
                    name = place.name?.toString() ?: "Getting location..."
                    Log.d("Place", "Place name: ${place.name}")
                    break
                }
            } else {
                //prints out the actual location of the person
                name = R.string.location_info.toString()
            }
        }
        return name
    }

}
