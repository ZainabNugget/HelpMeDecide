package com.griffith.helpmedecide

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.griffith.helpmedecide.databinding.ActivityMainTrialBinding

class HomePage : AppCompatActivity() {
    private lateinit var binding : ActivityMainTrialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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