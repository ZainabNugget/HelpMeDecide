package com.griffith.helpmedecide

/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.colorResource

//set up some colors
val ofF_white : Int = R.color.off_white
val brown : Int = R.color.brown
//set up the main font
val CustomFontFamily = FontFamily(
    Font(R.font.dragon_hunter)
)
val dark_blue : Int = R.color.dark_blue_custom

class GenerateList : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //set up the database manager (SQLite)
            val databaseManager = DatabaseManager(this)
            val customTypography = Typography(
                titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = CustomFontFamily),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = CustomFontFamily),
            )
            val darkBlue = colorResource(id = R.color.dark_blue_custom)
            //Will switch the intent to the spin the wheel activity
            MaterialTheme(typography = customTypography) {
                //scaffold is best
              Scaffold(
                  modifier = Modifier.fillMaxSize(),
                  topBar = {
                      //top bar set up
                      TopAppBar(
                          modifier = Modifier.fillMaxWidth(),
                          colors = TopAppBarDefaults.topAppBarColors(
                              containerColor = darkBlue,
                              titleContentColor = Color(LocalContext.current.getColor(ofF_white)),
                          ),
                          title = {
                              Box(
                                  modifier = Modifier.fillMaxWidth(),
                                  contentAlignment = Alignment.Center,
                              ) {
                                  Text(
                                      text = "Generate a list below!",
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
                      //bottom bar setup
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
                                val intent = Intent(this@GenerateList, HomePage::class.java)
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
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .background(Color(LocalContext.current.getColor(brown)))
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
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
                                //creating lists with the Wheel tag
                                CreateList("Wheel","Items",databaseManager) { list ->
                                    val intent = Intent(this@GenerateList, SpinTheWheel::class.java)
                                    intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(list))
                                    intent.putExtra("IS_USER_GENERATED", true)
                                    startActivity(intent)
                                }
                            }
                          Spacer(modifier = Modifier.height(10.dp))
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
                              //show all wheel specific items
                              ShowPreviousLists(db = databaseManager, "Wheel") { list ->
                                  val intent = Intent(this@GenerateList, SpinTheWheel::class.java)
                                  intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(list))
                                  intent.putExtra("IS_USER_GENERATED", true)
                                  startActivity(intent)
                              }
                          }
                    }
                  }
              )
            }
        }
    }
}

/*
* This method is used by the RollTheDice class as well
* The tag allows this to be multiusage
* when the list is completed we can specify what it does
* kind of like when a form is submitted
* */
@Composable
fun CreateList(tag: String, string : String, db : DatabaseManager, onListComplete: (List<String>) -> Unit) {
    var numberOfItems by remember { mutableStateOf("") }//number of items within a list
    var itemsCount by remember { mutableIntStateOf(0) } //how many items
    val items = remember { mutableStateListOf<String>() } //items themselves
    var newItem by remember { mutableStateOf("") } //get the item from the text field
    var listName by remember { mutableStateOf("") } //get the name of the list
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(LocalContext.current.getColor(dark_blue)))
    ) {
        TextField(
            value = listName,
            onValueChange = { input ->
                listName = input
            },
            label = { Text("Enter your list's name:") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        //Speciify the amount of things/people
        TextField(
            value = numberOfItems,
            onValueChange = { input ->
                numberOfItems = input
                itemsCount = input.toIntOrNull() ?: 0
            },
            label = { Text("Enter Amount Of $string(s)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        )
        //counter
        Text(
            text = "Number of $string left to create: $itemsCount",
            color = Color(LocalContext.current.getColor(ofF_white)),
            modifier = Modifier.padding(20.dp)
        )

        if (items.size < itemsCount) {
            TextField(
                value = newItem,
                onValueChange = { newItem = it },
                label = { Text("Add $string") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                    contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                ),
                onClick = {
                    if (newItem.isNotEmpty()) {
                        items.add(newItem)
                        newItem = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                enabled = items.size < itemsCount
            ) {
                Text("Add $string")
            }

            Text(
                text = "$string added: ${items.size} / $itemsCount",
                color = Color(LocalContext.current.getColor(ofF_white)),
                modifier = Modifier.padding(20.dp)
            )
        } else if (items.size == itemsCount && itemsCount > 0) {
            //button will add the list to database and do extra things
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                    contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                ),
                onClick = {
                    addToDatabase(tag, listName, items.toList(), db)
                    onListComplete(items.toList())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Complete List")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        //so the user knows where they are at
        Text(
            "Current List:",
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally),
            color = Color(LocalContext.current.getColor(ofF_white)),
            style = MaterialTheme.typography.bodyLarge
        )

        items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                TextField(
                    value = item,
                    onValueChange = { updatedValue -> items[index] = updatedValue },
                    label = { Text("Item ${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                        contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                    ),
                    onClick = { items.removeAt(index) },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                ) {
                    Text("X")
                }
            }
        }
    }
}

fun addToDatabase(tag: String, name : String, list : List<String>, db:DatabaseManager){
    //add the list with the tag and name of the list
    val stringList = list.joinToString(":")
    Log.i("ListString", stringList)
    db.addList(name, stringList, tag) //add to database :3
}

@Composable
fun ShowPreviousLists(db : DatabaseManager, type : String, onListComplete: (List<String>) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(LocalContext.current.getColor(dark_blue)))
            .padding(5.dp)
            .border(
                border = BorderStroke(
                    2.dp,
                    Color(LocalContext.current.getColor(brown))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Previous Lists:",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally),
            color = Color(LocalContext.current.getColor(ofF_white)),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        //use for loop to go through database
        db.getAllLists(type).asReversed().forEach { item ->
            //this prints out the lists based on the tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(LocalContext.current.getColor(R.color.gold)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //show lists
                Column(
                    modifier = Modifier.weight(1f)
                )  {
                    Text(
                        text = "${item.first}' List:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(LocalContext.current.getColor(R.color.off_white))
                    )
                    Text(
                        text = "Items: ${item.second}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(LocalContext.current.getColor(R.color.off_white))
                    )
                }
                //align to the end (the right)
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(LocalContext.current.getColor(R.color.light_gold)),
                            contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                        ),
                        onClick = {
                            //separate the list based on : and add to list
                            onListComplete(item.second.split(":"))
                        },//removes from the list
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .border(
                                border = BorderStroke(
                                    2.dp,
                                    Color(LocalContext.current.getColor(dark_blue))
                                ),
                                shape = RoundedCornerShape(25.dp)
                            )
                    ) {
                        Text("->", color = Color(LocalContext.current.getColor(R.color.off_white)))
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}

//Preview for all the components/composables
//@Preview (showBackground = true)
//@Composable
//fun PreviewLists(){
//    val db : DatabaseManager = DatabaseManager(LocalContext.current)
//    ShowPreviousLists(db)
//}

//@Preview (showBackground = true)
//@Composable
//fun PreviewList(){
//    val db = DatabaseManager(LocalContext.current)
//    CreateList(db) {  }
//}

//@Preview (showBackground = true)
//@Composable
//fun ExpandableCardListPreview(){
//    ExpandableCardList()
//}