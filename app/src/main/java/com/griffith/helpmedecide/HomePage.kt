package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {
    //explicitly ask to get permission to use location
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(this, LocationService::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show()
            }
        }
    private fun checkLocationPermission() {
        //I need to use fine location for using google places API
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //go to the location service class
            val intent = Intent(this, LocationService::class.java)
            startActivity(intent)
        } else {
            //request permission if permission wasn't granted
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    //will be further implemented in the third milestone...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //display the homepage
        ontoHomePage()
    }

    fun ontoHomePage(){
        //display the xml file
        setContentView(R.layout.activity_home_page)
        //get all the buttons from the layout file
        val rollTheDiceBtn : CardView = findViewById(R.id.rollTheDice)
        val ownListBtn : CardView = findViewById(R.id.ownListBtn)
        val locationServiceBtn : CardView = findViewById(R.id.locationServiceBtn)
        //go to roll the dice page
        rollTheDiceBtn.setOnClickListener {
            val intent = Intent(this, RollTheDice::class.java)
            startActivity(intent)
        }
        //go to generate list activity
        ownListBtn.setOnClickListener{
            val intent = Intent(this, GenerateList::class.java)
            startActivity(intent)
        }
        //go to location service activity
        locationServiceBtn.setOnClickListener{
            checkLocationPermission() //check permission before moving on :)
        }
        //set up the bottom nav bar
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    setContentView(R.layout.activity_home_page)
                    true
                }
                else -> false
            }
        }

    }
}