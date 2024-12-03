package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.color.utilities.Score

private const val SHAKE_THRESHOLD = 25
//to avoid excessive shaking :3
private const val Cooldown = 200L

class RollTheDice : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    //For drawinf the dice
    private val _result = mutableStateOf(1)
    private val _isRolling = mutableStateOf(false)
    private val _showDialog = mutableStateOf(false)
    private val result: State<Int> get() = _result
    //acceleration/seeing if we exceed threshold
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f
    //to calculate time difference
    var lastUpdatedTime : Long = 0
    var timeDifference : Long = 0

    private var currentPlayer = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //sensro manager to get the data

        val size = Data.size

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        setContent {
            //our main component
            DiceRollerScreen(
                result = result.value,
                isRolling = _isRolling.value,
                showDialog = _showDialog.value,
                onRoll = this::onRollCurrentPlayer,
                onDialogDismiss = { _showDialog.value = false }
            )
            ScoreBoard(Data)
        }

    }

    fun nextPlayer(){
        currentPlayer++ //increrment player
    }

    fun updateScores(index : Int, result : Int){ //replace
        val updatedList = Data.toMutableList()
        val name = Data[index].name
        val prev = Data[index].score
        updatedList[index] = Scores(name, prev + result)
        Data.clear()
        Data.addAll(updatedList)
    }

    fun onRollCurrentPlayer(){
        if (currentPlayer >= Data.size) return
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdatedTime > Cooldown) {
            val roll = (1..6).random()
            _result.value = roll
            updateScores(currentPlayer, roll)
        }
    }

    override fun onPause() {
        //when the device isnt moving
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onResume() {
        //when they sense a change
        super.onResume()
        sensorManager?.registerListener(
            this,
            //we're using TYPE acceleeromete
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun onRoll() {
        //called whenevber the system detects shaking
        val currentTime = System.currentTimeMillis()
        if(currentTime - lastUpdatedTime > Cooldown){
            //randomsises from 1 to 6
            _result.value = (1..6).random()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && !_isRolling.value) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val currentTime = System.currentTimeMillis()
            if(currentTime - lastUpdatedTime > Cooldown){
                timeDifference = currentTime - lastUpdatedTime
                val currentAcceleration = Math.abs(x+y+z - last_x+last_y+last_z/timeDifference * 1000)
                if(currentAcceleration > SHAKE_THRESHOLD){
                    onRoll()
//                    _showDialog.value = true
                }
                //update values after sensor shakes
                lastUpdatedTime = currentTime
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO()
    }
}

@Composable
fun DiceRollerScreen(
    result: Int,
    isRolling: Boolean,
    showDialog : Boolean,
    onRoll: () -> Unit,
    onDialogDismiss: () -> Unit
) {
    //for potential animation
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    //using images i drew
    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
    //will implement firther, shows alot of errors
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDialogDismiss() },
            confirmButton = {
                TextButton(onClick = { onDialogDismiss() }) {
                    Text("OK")
                }
            },
            title = { Text("Shake Detected") },
            text = { Text("The dice rolled: $result") }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.graphicsLayer(
                rotationZ = rotation.value,
                scaleX = scale.value,
                scaleY = scale.value
            )
        ) {
            Image(
                //paint the dice
                painter = painterResource(imageResource),
                contentDescription = "Dice showing $result"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onRoll()
            },
            enabled = !isRolling
        ) {
            Text(if (isRolling) "Rolling..." else stringResource(R.string.roll))
        }

        Spacer(modifier = Modifier.height(10.dp))
        //resulting dice
        Text("Your rolled dice: $result")
    }
}

data class Scores(
    val name: String,
    val score: Int
)

val Data = mutableStateListOf(
    Scores("Alice", 0),
    Scores("Eric", 0),
    Scores("Alice", 0),
    Scores("Eric", 0),
    Scores("Alice", 0),
    Scores("Eric", 0)
)

@Composable
fun ScoreBoard(Data : List<Scores>){
    //horizontal scroll, based on the amount of people in the list
    //for loop that goes throught the peaople and creates
    Column {
        Text("ScoreBoard")
        LazyRow(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Data.size) { //loop through the people
                index -> ScoreCard(Data[index])
            }
        }
    }
}

@Composable
fun ScoreCard(score : Scores){
    Card (
        modifier = Modifier.size(width = 80.dp, height = 80.dp)
    ){
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text=score.name)
            Text(text ="${score.score}")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewScoreBoard() {
    MaterialTheme {
        ScoreBoard(Data)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewDiceCanvas() {
//    MaterialTheme {
//        DiceRollerScreen(result = 1, isRolling = false, showDialog = false, onRoll = {})
//    }
//}
