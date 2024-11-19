package com.griffith.helpmedecide

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class RollTheDice : ComponentActivity() {
    private var sensorManager: SensorManager? = null
    private var isRolling = false
    private val shakeThreshold = 7.0f // Threshold for detecting shake

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerScreen()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.let {
            sensorManager?.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(sensorEventListener)
    }

    private val sensorEventListener = object : SensorEventListener {
        private var lastAcceleration = 0f

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                var x = event.values[0]
                var y = event.values[1]
                var z = event.values[2]

                val currentAcceleration = sqrt(x * x + y * y + z * z)
                val delta = currentAcceleration - lastAcceleration
                lastAcceleration = currentAcceleration

                if (delta > shakeThreshold && !isRolling) {
                    isRolling = true
                    DiceRollerState.triggerShakeRoll() // Trigger dice roll
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}

object DiceRollerState {
    private var onShakeRoll: (() -> Unit)? = null

    fun registerShakeRollListener(listener: () -> Unit) {
        onShakeRoll = listener
    }

    fun triggerShakeRoll() {
        onShakeRoll?.invoke()
    }
}

@Composable
fun DiceRollerScreen() { //Will contain all info about the dice
    var result by remember { mutableIntStateOf(1) } //result of the dice roll
    var isRolling by remember { mutableStateOf(false) }

    val rotation = remember { Animatable(0f) } //rotation for animation
    val scale = remember { Animatable(1f) } //scale for animation

    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    val coroutineScope = rememberCoroutineScope()

    DiceRollerState.registerShakeRollListener {
        if (!isRolling) {
            coroutineScope.launch {
                animateDiceRoll(rotation, scale) {
                    result = (1..6).random()
                    isRolling = false
                }
            }
        }
    }
    //Layout :-)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = rotation.value,
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        ) {
            Image(
                painter = painterResource(imageResource),
                contentDescription = result.toString()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { //help animation and stuffs
                if (!isRolling) {
                    coroutineScope.launch {
                        animateDiceRoll(rotation, scale) {
                            result = (1..6).random()
                            isRolling = false
                        }
                    }
                }
            },
            enabled = !isRolling
        ) {
            Text(if (isRolling) "Rolling..." else stringResource(R.string.roll))
        }
        //add some spacing to look better
        Spacer(modifier = Modifier.height(10.dp))
        Text("Your chosen dice was.... $result") //display the result (for now)
    }
}
//Animation handling, (rotating the dice)
suspend fun animateDiceRoll(
    rotation: Animatable<Float, *>,
    scale: Animatable<Float, *>,
    onComplete: () -> Unit
) {
    rotation.animateTo(
        targetValue = 360f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )
    rotation.snapTo(0f)

    onComplete()
}

@Preview(showBackground = true)
@Composable
fun PreviewDiceCanvas() {
    MaterialTheme {
        DiceRollerScreen()
    }
}
