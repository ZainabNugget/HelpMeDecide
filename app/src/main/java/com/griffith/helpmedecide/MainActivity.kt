package com.griffith.helpmedecide

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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


//Variables
var xSpeed = mutableFloatStateOf(0f)
var ySpeed = mutableFloatStateOf(0f)
var zSpeed = mutableFloatStateOf(0f)
var card_width = 450.dp
var card_height = 150.dp
var font_size = 15.sp

private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
//Main Class
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            try {
//                requestPermissionLauncher = registerForActivityResult(
//                    ActivityResultContracts.RequestPermission()
//                ) { isGranted: Boolean ->
//                    if (isGranted) {
//                        print("granted")
//                    } else {
//                        print("not granted ippe")
//                    }
//                }
//            } catch (e: IllegalStateException) {
//                TODO("Not yet implemented")
//            }
//            i = ContextCompat.checkSelfPermission(LocalContext.current, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//            // TODO(Go to a different activity!)
//            val intent = Intent(this, SpinTheWheel::class.java)
//            // to go to activity startActivity(intent)
//
//            val context = LocalContext.current
//            val sensorManager : SensorManager =
//                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//            val navController = rememberNavController()

            // TODO(Accelerometer sensor check!) Will be used somewhere else
//            SensorExists(sensorManager, Sensor.TYPE_ACCELEROMETER)

            // TODO(Implement the GPS location service here)
            //
            NavigationScreen() //not sure if this is an ok implementation honestly
            val context = LocalContext.current
            val intent = Intent(this, GPS::class.java)
//            Column {
//                Button(onClick = {
//
//
//                    context.startActivity(intent)
//                }) {
//                    Text("Open GPS")
//                }
//            }

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
                xSpeed.floatValue = event.values[0]
                ySpeed.floatValue = event.values[1]
                zSpeed.floatValue = event.values[2]
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//            print(accuracy)
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

//TODO(Create the home screen + Explicit intents towards the other activities)
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
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            val context = LocalContext.current
            InfoCards("Welcome", Color.White, card_width, card_height + 20.dp)

            //Introcuction text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What are we deciding today?",
                    textAlign = TextAlign.Center // Center text inside the Text composable
                )
            }

            //Clickable boxes that take you to the activities
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            val intent = Intent(context, RollTheDice::class.java)
                            context.startActivity(intent)
                        },
                        indication = rememberRipple(bounded = true, color = Color.Gray),
                        interactionSource = remember { MutableInteractionSource() }
                    )

            ) {
                InfoCards("Who's gonna do it?!", Color.White, card_width, card_height)
            }
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            val intent = Intent(context, SpinTheWheel::class.java)
                            context.startActivity(intent)
                        },
                        indication = rememberRipple(bounded = true, color = Color.Gray),
                        interactionSource = remember { MutableInteractionSource() }
                    )

            ){
                InfoCards("What should i eat?!", Color.White, card_width, card_height)
            }
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            val intent = Intent(context, GenerateList::class.java)
                            context.startActivity(intent)
                        },
                        indication = rememberRipple(bounded = true, color = Color.Gray),
                        interactionSource = remember { MutableInteractionSource() }
                    )

            ){
                InfoCards("Generate My Own", Color.White, card_width, card_height)
            }
        }
    }
}

//TODO(Set up the implicit intents homescreen/settingsscreen)
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