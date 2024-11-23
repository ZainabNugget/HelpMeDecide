package com.griffith.helpmedecide

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
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

private const val shakeThreshold = 4.0f //when we exceed this we trigger the dice roll
private var isRolling = false //to check if the dice is rolling

class RollTheDice : ComponentActivity() {
    private var sensorManager: SensorManager? = null
    private val _result = mutableStateOf(1) // 1 to 6 (dice)
    private val result: State<Int> get() = _result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //Our main UI, we pass in the result (dice)
            DiceRollerScreen(result = result.value, onRoll = this::onRoll)
        }

        DiceRollerState.registerShakeRollListener {
            if (isRolling) {
                onRoll()
            }
        }

        //Get our sensor (Accelerometer)
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

    private fun onRoll() {
        if (!isRolling) {
            isRolling = true
            _result.value = (1..6).random()
            isRolling = false
        }
    }
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
                DiceRollerState.triggerShakeRoll()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

object DiceRollerState {
    private var onShakeRoll: (() -> Unit)? = null

    fun registerShakeRollListener(listener: () -> Unit) {
        onShakeRoll = listener
    }

    fun triggerShakeRoll() {
        onShakeRoll?.invoke() ?: Log.e("DiceRollerState", "No listener registered for shake roll.")
    }
}
@Composable
fun DiceRollerScreen(result: Int, onRoll: () -> Unit) {
    var isRolling by remember { mutableStateOf(false) }
    //for animation
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    //will use images to draw different dice
    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isRolling) {
        if (isRolling) {
            Log.d("DiceRoller", "Starting dice roll animation...")
            coroutineScope.launch {
                animateDiceRoll(rotation, scale) {
                    onRoll()
                    isRolling = false
                    Log.d("DiceRoller", "Dice roll animation completed. Result: $result")
                }
            }
        }
    }

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
            onClick = {
                if (!isRolling) {
                    isRolling = true
                    coroutineScope.launch {
                        animateDiceRoll(rotation, scale) {
                            onRoll()
                            isRolling = false
                        }
                    }
                }
            },
            enabled = !isRolling
        ) {
            Text(if (isRolling) "Rolling..." else stringResource(R.string.roll))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Your chosen dice was.... $result")
    }
}

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
        DiceRollerScreen(result = 1, onRoll = {})
    }
}
