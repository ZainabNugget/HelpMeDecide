package com.griffith.helpmedecide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                //maybe log in and log out activities?
                //when you go to the profile page, it checks if youre logged in, if you are
                //you are taken to the profile
                //other wise you log in?

            }
        }
    }
}