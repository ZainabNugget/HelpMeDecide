package com.griffith.helpmedecide

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Variables
var xSpeed = mutableStateOf(0f)
var ySpeed = mutableStateOf(0f)
var zSpeed = mutableStateOf(0f)
var card_width = 450.dp;
var card_height = 150.dp;


//Main Class
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO(Go to a different activity!)
            val intent = Intent(this, SpinTheWheel::class.java)
            // to go to activity startActivity(intent)

            val context = LocalContext.current
            val sensorManager : SensorManager =
                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val navController = rememberNavController();

            // TODO(Accelerometer sensor check!) Will be used somewhere else
            SensorExists(sensorManager, Sensor.TYPE_ACCELEROMETER)

            // TODO(Implement the GPS location service here)
            //
            NavigationScreen() //not sure if this is an ok implementation honestly :3

        }
    }

    // TODO(Add all methods for sensor use (Will be placed here for now))
    @Composable
    private fun SensorExists(sensorManager: SensorManager, type : Int) {
        if (sensorManager.getDefaultSensor(type) != null) {
            val accelerometer = sensorManager.getDefaultSensor(type)
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private val sensorEventListener : SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
                xSpeed.value = event.values[0];
                ySpeed.value = event.values[1];
                zSpeed.value = event.values[2];
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            print(accuracy)
        }
    }

}

// TODO(Implement a template for info cards, will be clickable!)
@Composable
fun InfoCards(text : String, color: Color, width : Dp, height : Dp){
    Card(
        border = BorderStroke(1.dp, color),
        modifier = Modifier
            .size(width = width, height = height)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

// TODO(Navigation bottom bar)
@Composable
fun NavigationScreen(){
    val navController = rememberNavController()
    Surface {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { HomeScreen(navController) } 
            composable("settings") { SettingsScreen(navController) }
        }
    }
}

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
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            InfoCards("Change The Theme!",
                Color.White, card_width, card_height)
        }
    }

}

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Row { }
        },
        bottomBar = {
            BottomBarNav(navController)
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Text(xSpeed.value.toString())
            InfoCards("Welcome", Color.White, card_width, card_height * 2);
            Row { // Need to center horizontally
                Text(text = "What are we deciding today?")
            }
            for (i in 1..4) {
                InfoCards("Sample" + i, Color.White, card_width, card_height)
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