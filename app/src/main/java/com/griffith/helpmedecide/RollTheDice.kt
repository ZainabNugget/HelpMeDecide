package com.griffith.helpmedecide

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

//https://developer.android.com/codelabs/basic-android-kotlin-compose-build-a-dice-roller-app#0
//Will use that to further develop the dice animations
//Gonna use accelerometer to trigger the dice animations
class RollTheDice : ComponentActivity() {
    private var sensorManager: SensorManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.LightGray
            ) {
                DrawTheDice()
            }
        }
        //Get the sensor manager with context by checking if the device has it :)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.let {
            sensorManager?.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    //Get rid of it when we're done!
    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(sensorEventListener)
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            print("Yay the sensor changed")
        }
    }
}
//Simple Dice Drawwing :3
//Will implement it in pictures and stuff later
//Using xml, because its easier to animate that way!
@Composable
fun DrawTheDice(){
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasSize = size.minDimension / 2f
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        drawRect(
            color = Color.White,
            size = androidx.compose.ui.geometry.Size(canvasSize, canvasSize),
            topLeft = Offset(centerX - canvasSize / 2, centerY - canvasSize / 2)
        )

        val dotRadius = 30f
        drawCircle(
            color = Color.Black,
            radius = dotRadius,
            center = Offset(centerX, centerY)
        )
    }
}

//Easy debugging
@Preview(showBackground = true)
@Composable
fun PreviewDiceCanvas() {
    MaterialTheme {
        DrawTheDice()
    }
}

