package com.griffith.helpmedecide
/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.random.Random

private const val SHAKE_THRESHOLD = 15//to avoid excessive shaking
private const val Cooldown = 200L //to wait a bit
var peopleList : List<String>?= null //list of people upon creation
//var index = 0 //index of people in the list
var size = 0//size of the list

class RollTheDice : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private val isRolling = mutableStateOf(false)
    private val diceNumber = mutableStateOf(1)
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f
    //to calculate time difference
    var lastUpdatedTime : Long = 0
//    var timeDifference : Long = 0
//    var numberOfPeople = 0

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setup the sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        setContent {
            //setup the database manager
            val db = DatabaseManager(this)
            //typography using the dragon hunter font
            val customTypography = Typography(
                titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = CustomFontFamily),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = CustomFontFamily),
            )
            MaterialTheme (typography = customTypography) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(LocalContext.current.getColor(R.color.dark_blue_custom)),
                                titleContentColor = Color(LocalContext.current.getColor(ofF_white)),
                            ),
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "ROLL THE DICE!",
                                        modifier = Modifier
                                            .padding(vertical = 8.dp),
                                        color = Color(LocalContext.current.getColor(R.color.off_white)),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color(LocalContext.current.getColor(R.color.light_gold)),
                            contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                IconButton(onClick = {
                                    val intent = Intent(this@RollTheDice, HomePage::class.java)
                                    startActivity(intent)
                                }) {
                                    Column (
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                                    }
                                }
                            }
                        }
                    },
                    content = { paddingValues ->
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .background(Color(LocalContext.current.getColor(brown)))
                                .padding(16.dp)
                        ){
                            NavigationScreen(isRolling, diceNumber, db, this@RollTheDice)
                        }
                    }
                )

            }
        }
    }
    //what happens when the roll is pressed
    private fun onRoll() {
        if (!isRolling.value) {
            isRolling.value = true
            diceNumber.value = (1..20).random()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            //setting up the threshold of the shaking
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdatedTime > Cooldown) {
                val deltaTime = currentTime - lastUpdatedTime
                val acceleration = Math.abs(x + y + z - last_x - last_y - last_z) / deltaTime * 1000

                if (acceleration > SHAKE_THRESHOLD) {
                    //debugging
                    Log.i("Shake", "Shake threshold reached!")
                    onRoll()
                }

                lastUpdatedTime = currentTime
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
@Composable
fun NavigationScreen(isRolling: MutableState<Boolean>, diceNumber: MutableState<Int>, db: DatabaseManager, context: Context){
    //for implicit navigation
    val navController = rememberNavController()
    Surface (
        modifier = Modifier.fillMaxWidth()
    ){
        NavHost(
            navController = navController,
            startDestination = "List"
        ){
            composable("List"){
                CreatePeopleList(navController, db, context)
            }
            composable("RollDice"){
                DiceRoller(isRolling, diceNumber, navController)
            }
        }
    }
}

@Composable
fun DiceRoller(
    isRolling: MutableState<Boolean>,
    diceNumber: MutableState<Int>,
    navController: NavController
) {
    //variables for dice rolling
    val rotation = remember { Animatable(0f) } //rotation of the dice
    val showDialog = remember { mutableStateOf(false) } //shows dialog boolean
    val showWinnerDialog = remember { mutableStateOf(false) } //winner dialog
    val winner = remember { mutableStateOf<List<Pair<String, Int>>?>(null) } //winner condition
    val currentPlayerIndex = remember { mutableStateOf(0) } //index of curent player for winning condition
    val players = remember { //get the list of players
        peopleList?.map { name -> name to 0 }?.toMutableStateList() ?: mutableStateListOf()
    }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(context.getColor(R.color.dark_blue_custom))),
        contentAlignment = Alignment.Center
    ) {
        //text to prompt phone shake if its not rolling already
        if (!isRolling.value) {
            Text(
                text = "Shake your phone & roll the dice!",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                color = Color(context.getColor(R.color.off_white)),
                style = TextStyle(fontSize = 16.sp, fontFamily = dragonHunterFont)
            )
        }
        //players list is empty then no need for scoreboard
        if (players.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .height(200.dp)//height of the scoreboard
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            ) {
                ScoreBoard(players = players)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            //rolls the dice
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dicetwenty),
                    contentDescription = "Dice",
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer(rotationZ = rotation.value)
                )
                Text(
                    text = if (isRolling.value) "..." else diceNumber.value.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(context.getColor(R.color.dark_blue_custom))
                )
            }

            //styling
            Spacer(modifier = Modifier.height(20.dp))

            //button to roll the dice if shaking doesn't work
            Button(
                onClick = {
                    if (!isRolling.value) {
                        isRolling.value = true
                        diceNumber.value = (1..20).random()
                    }
                },
                enabled = !isRolling.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(context.getColor(R.color.gold)),
                    contentColor = Color(context.getColor(R.color.off_white))
                )
            ) {
                Text("Roll Dice")
            }
        }
    }

    //handle the animation
    LaunchedEffect(isRolling.value) {
        if (isRolling.value) {
            // Animate the dice roll
            rotation.animateTo(
                targetValue = 720f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
            rotation.snapTo(0f)

            //update the scores
            val roll = diceNumber.value
            //handles winning condition
            if (players.isNotEmpty()) {
                val currentPlayer = players[currentPlayerIndex.value]
                players[currentPlayerIndex.value] =
                    currentPlayer.copy(second = currentPlayer.second + roll)

                //check if this is the last player
                if (currentPlayerIndex.value == players.lastIndex) {
                    //if last player, then see who's the winner!
                    val maxScore = players.maxOfOrNull { it.second }
                    winner.value = players.filter { it.second == maxScore }

                    //show the winner dialog
                    showWinnerDialog.value = true
                } else {
                    //move to the next player
                    currentPlayerIndex.value = (currentPlayerIndex.value + 1) % players.size
                }
            }
            isRolling.value = false //stops rolling
            showDialog.value = true
        }
    }

    //show the dialog when the dice stops rolling
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            containerColor = Color(context.getColor(R.color.gold)),
            textContentColor = Color(context.getColor(R.color.off_white)),
            titleContentColor = Color(context.getColor(R.color.off_white)),
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(context.getColor(R.color.dark_blue_custom)),
                        contentColor = Color(context.getColor(R.color.off_white))
                    ),
                    onClick = { showDialog.value = false }
                ) {
                    Text("OK", style = TextStyle(fontFamily = dragonHunterFont))
                }
            },
            text = { Text("You rolled ${diceNumber.value}", fontSize = 20.sp, style = TextStyle(fontFamily = dragonHunterFont)) }
        )
    }

    //show the winner
    if (showWinnerDialog.value) {
        AlertDialog(
            onDismissRequest = { showWinnerDialog.value = false },
            containerColor = Color(context.getColor(R.color.gold)),
            textContentColor = Color(context.getColor(R.color.off_white)),
            titleContentColor = Color(context.getColor(R.color.off_white)),
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(context.getColor(R.color.dark_blue_custom)),
                        contentColor = Color(context.getColor(R.color.off_white))
                    ),
                    onClick = { showWinnerDialog.value = false }
                ) {
                    Text("OK", style = TextStyle(fontFamily = dragonHunterFont))
                }
            },
            text = {
                Text(
                    text = if (winner.value?.size == 1) {
                        "Winner: ${winner.value!!.first().first} with ${winner.value!!.first().second} points!"
                    } else {
                        "Winners: ${
                            winner.value!!.joinToString { "${it.first} (${it.second} points)" }
                        }"
                    },
                    fontSize = 20.sp,
                    style = TextStyle(fontFamily = dragonHunterFont)
                )
            }
        )
    }
}


@Composable
fun ScoreBoard(players: List<Pair<String, Int>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .size(200.dp)
            .padding(16.dp)
            .background(Color(LocalContext.current.getColor(R.color.light_gold)))
            .border(
                border = BorderStroke(
                    2.dp,
                    Color(LocalContext.current.getColor(brown))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = "Scoreboard",
            fontSize = 24.sp,
            modifier = Modifier.padding(6.dp)
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            //display the scoreboard based on the players :)
            players.forEach { (name, score) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name, fontSize = 18.sp)
                    Text(text = "Score: $score", fontSize = 18.sp)
                }
            }
        }
    }
}

//this method creates the people's list
@Composable
fun CreatePeopleList(navController: NavController, db: DatabaseManager, context: Context){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color(LocalContext.current.getColor(R.color.brown))),
        horizontalArrangement = Arrangement.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //telling the user what to do
            Text(
                "Make a List of people who are playing, then click complete list to play.",
                modifier = Modifier
                    .padding(2.dp),
                textAlign = TextAlign.Center,
                color = Color(LocalContext.current.getColor(ofF_white)),
                style = MaterialTheme.typography.bodyLarge
            )
            PeopleListScreen(navController, db)
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(LocalContext.current.getColor(R.color.dark_blue_custom)))
                    .border(
                        border = BorderStroke(
                            2.dp,
                            Color(LocalContext.current.getColor(R.color.gold))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                verticalArrangement = Arrangement.Center
            ){
                //added an option to just go use the dice
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(text = "If you want to skip making a list ->",
                        textAlign = TextAlign.Center,
                        color = Color(LocalContext.current.getColor(ofF_white)))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.border(
                            border = BorderStroke(
                                2.dp,
                                Color(LocalContext.current.getColor(R.color.dark_blue_custom))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                            contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                        ),
                        onClick = {
                            navController.navigate("RollDice")
                        }
                    ) {
                        Text(text = "Take me to the dice", style = TextStyle(fontFamily = dragonHunterFont))
                    }
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(LocalContext.current.getColor(R.color.dark_blue_custom)))
                    .border(
                        border = BorderStroke(
                            2.dp,
                            Color(LocalContext.current.getColor(R.color.gold))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                verticalArrangement = Arrangement.Center
            ){
                //If user has made lists before, show them so the user can go there
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(
                                2.dp,
                                Color(LocalContext.current.getColor(brown))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ShowPreviousLists(db = db, "Dice") { list ->
                        peopleList = list
                        navController.navigate("RollDice")
                        size = list.size
                    }
                }
            }
        }
    }
}

//this uses the create list method from the generate list class
@Composable
fun PeopleListScreen(navController: NavController, db:DatabaseManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    2.dp,
                    Color(LocalContext.current.getColor(R.color.gold))
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        CreateList("Dice", "Person", db) { list ->
            peopleList = list
            navController.navigate("RollDice")
            size = list.size
        }
    }
}

//preview and debugging
@Preview
@Composable
fun PreviewScoreBoard(){
    ScoreBoard(listOf(Pair("Alice", 0),
        Pair("Bob", 0),
        Pair("Charlie", 0)))
}
//@Preview(showBackground = true)
//@Composable
//fun PreviewDiceRoll() {
//    MaterialTheme {
//        DiceRoller() { }
//    }
//}
