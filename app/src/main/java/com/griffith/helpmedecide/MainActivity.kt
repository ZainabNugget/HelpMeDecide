package com.griffith.helpmedecide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController();

            HomeScreen(navController);
            Column {
                InfoCards("Welcome", Color.White, card_width, card_height * 2);
                Row { // Need to center horizontally
                    Text(text = "What are we deciding today?")
                }
                //Cards
                for (i in 1..3){
                    InfoCards("Sample" + i, Color.White, card_width, card_height)
                }
            }
        }
    }
}

//It will have cards like this, scrollable (add later)
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

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Row { }
        },
        bottomBar = {
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
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {}
    }
}

//Call when ever we want to inform the user of something!
@Composable
fun InfoPage(title : String, body: String){
    Surface (
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        )
        {
            Row { Text(text = title,
                fontSize = 40.sp
                ) }
            Row{
                Text(text = body,
                    fontSize = 20.sp)
            }
            var checked = remember { mutableStateOf(false) }
            Row {
                Checkbox(checked = checked.value, onCheckedChange = { checked.value = it })
                Text("Do not show this again!")
            }

            Button(
                onClick = {}, //continue to activity!
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
                ) {
                Text("Got it")
            }
        }
    }
}
