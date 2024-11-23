package com.griffith.helpmedecide

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

var card_width = 450.dp
var card_height = 150.dp
var font_size = 15.sp

class MainActivity : ComponentActivity() {
    //explicitly ask to get permission to use location
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(this, SpinTheWheel::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationScreen {checkLocationPermission()} //not sure if this is an ok implementation honestly
        }
    }

    private fun checkLocationPermission() {
        //Not gonna use a fine location, a general location is fine
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(this, SpinTheWheel::class.java)
            startActivity(intent)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}

@Composable
fun InfoCards(text : String, color: Color, width : Dp, height : Dp){
    Card(
        border = BorderStroke(1.dp, color),
        modifier = Modifier
            .size(width = width, height = height)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(20.dp),
                fontSize = font_size
            )
        }

    }
}

@Composable
fun NavigationScreen(onCheckLocationPermission: () -> Unit) {
    val navController = rememberNavController()
    Surface {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { HomeScreen(navController, onCheckLocationPermission) }
            composable("settings") { SettingsScreen(navController) }
        }
    }
}

//TODO(Create the settings screen)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Row { }
        },
        bottomBar = {
            BottomBarNav(navController)
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            //Will implement a dark/light theme
            InfoCards("Change The Theme!",Color.White, card_width, card_height)
            //Increases the font size for accesibility
            InfoCards("Increase font size",Color.White, card_width, card_height)
            //Giving the option to turn off location for the user
            InfoCards("Turn off location!",Color.White, card_width, card_height)
            //Can delete data :3
            InfoCards("Delete my data!",Color.White, card_width, card_height)
        }
    }

}

@Composable
fun HomeScreen(navController: NavController, onCheckLocationPermission: () -> Unit) {
    Scaffold(
        topBar = { Row { } },
        bottomBar = { BottomBarNav(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            val context = LocalContext.current
            InfoCards("Welcome", Color.White, card_width, card_height + 20.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What are we deciding today?",
                    textAlign = TextAlign.Center
                )
            }

            //Clickable boxes that take you to the activities
            Box(
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent(context, LocationService::class.java)
                        context.startActivity(intent)
                    },
                    indication = rememberRipple(bounded = true, color = Color.Gray),
                    interactionSource = remember { MutableInteractionSource() }
                )
            ) {
                InfoCards("Who's gonna do it?!", Color.White, card_width, card_height)
            }

            //Location Permission and Activity
            Box(
                modifier = Modifier.clickable(
                    onClick = { onCheckLocationPermission() },
                    indication = rememberRipple(bounded = true, color = Color.Gray),
                    interactionSource = remember { MutableInteractionSource() }
                )
            ) {
                InfoCards("Location Generated", Color.White, card_width, card_height)
            }

            Box(
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent(context, GenerateList::class.java)
                        context.startActivity(intent)
                    },
                    indication = rememberRipple(bounded = true, color = Color.Gray),
                    interactionSource = remember { MutableInteractionSource() }
                )
            ) {
                InfoCards("Generate My Own", Color.White, card_width, card_height)
            }
        }
    }
}

@Composable
private fun BottomBarNav(navController: NavController) {
    BottomAppBar {
        IconButton(onClick = {
            navController.navigate("home")
        }) {
            Icon(Icons.Filled.Home, contentDescription = "Home")
        }
        IconButton(onClick = {
            navController.navigate("settings")
        }) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}