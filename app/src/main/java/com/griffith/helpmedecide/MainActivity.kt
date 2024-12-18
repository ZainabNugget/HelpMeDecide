package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //simple setting content view of the main intro page
        setContentView(R.layout.activity_main_trial)
        val introButton : Button = findViewById(R.id.introBtn)
        introButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }
}