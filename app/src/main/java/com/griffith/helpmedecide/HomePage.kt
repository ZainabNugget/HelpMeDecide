package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        //Not gonna use a fine location, a general location is fine
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(this, LocationService::class.java)
            startActivity(intent)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    //will be further implemented in the third milestone...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //get all the buttons
        val rollTheDiceBtn : CardView = findViewById(R.id.rollTheDice)
        val ownListBtn : CardView = findViewById(R.id.ownListBtn)
        val locationServiceBtn : CardView = findViewById(R.id.locationServiceBtn)
        //go to roll the dice page
        rollTheDiceBtn.setOnClickListener {
            val intent = Intent(this, RollTheDice::class.java)
            startActivity(intent)
        }
        //go to geenerate list activity
        ownListBtn.setOnClickListener{
            val intent = Intent(this, GenerateList::class.java)
            startActivity(intent)
        }
        //go to location service activity
        locationServiceBtn.setOnClickListener{
            checkLocationPermission() //check permission before moving on :)
        }

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnClickListener { item ->
            when (item.id) {
                R.id.nav_home -> {
                    setContentView(R.layout.activity_home_page)
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SpinTheWheel::class.java)
                    startActivity(intent)
                }
                else -> false
            }
        }
    }
}