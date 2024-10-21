package com.griffith.helpmedecide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
var card_width = 450.dp;
var card_height = 150.dp;
class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController();
            HomeScreen(navController);
            Column {
                InfoCards("Welcome", Color.White, card_width, card_height * 2);
                //Cards
                for (i in 1..3){
                    InfoCards("Sample" + i, Color.White, card_width, card_height)
                }
            }
        }
    }
}


//@Composable
//fun infoCards(text : String, color: Color, width : Dp, height : Dp){
//    Card(
//        border = BorderStroke(1.dp, color),
//        modifier = Modifier
//            .size(width = width, height = height)
//            .padding(16.dp)
//    ) {
//        Text(
//            text = text,
//            modifier = Modifier
//                .padding(20.dp)
//                .align(Alignment.CenterHorizontally)
//        )
//    }
//}
//
//@Composable
//fun HomeScreen(navController: NavController) {
//    Scaffold(
//        topBar = {
//            Row { }
//        },
//        bottomBar = {
//            BottomAppBar {
//                IconButton(onClick = {
//                    navController.navigate("home")
//                }) {
//                    Icon(Icons.Filled.Home, contentDescription = "Home")
//                }
//                IconButton(onClick = {
//                    navController.navigate("settings")
//                }) {
//                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
//                }
//            }
//        }) { innerPadding ->
//        Column(
//            modifier = Modifier.padding(innerPadding),
//            verticalArrangement = Arrangement.spacedBy(30.dp)
//        ) {}
//    }
//}






