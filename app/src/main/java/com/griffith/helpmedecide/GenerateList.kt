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
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource

val ofF_white : Int = R.color.off_white
val gold : Int = R.color.gold
val CustomFontFamily = FontFamily(
    Font(R.font.dragon_hunter)
)

class GenerateList : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val databaseManager = DatabaseManager(this)
            val allData = databaseManager.getAllLists()
            allData.forEach { Log.i("Database", it.toString()) }
            val customTypography = Typography(
                titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = CustomFontFamily),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = CustomFontFamily),
            )
            val darkBlue = colorResource(id = R.color.dark_blue_custom)
            val lightGold = colorResource(id = R.color.light_gold)
            val lightBlue = colorResource(id = R.color.light_blue_custom)

            val backGroundColor : Int = LocalContext.current.getColor(R.color.dark_blue_custom)
            //Will switch the intent to the spin the wheel activity
            MaterialTheme(typography = customTypography) {
              Scaffold(
                  modifier = Modifier.fillMaxSize(),
                  topBar = {
                      LargeTopAppBar(
                          colors = TopAppBarDefaults.topAppBarColors(
                              containerColor = darkBlue,
                              titleContentColor = Color(LocalContext.current.getColor(ofF_white)),
                          ),
                          title = { Text("Generate List") }
                      )
                  },
                  bottomBar = {
                    BottomAppBar(
                        containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                        contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(this@GenerateList, HomePage::class.java)
                            startActivity(intent)
                        }) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                        }
                    }
                  },
                  content = { paddingValues ->
                      Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .background(Color(backGroundColor))
                            .padding(paddingValues)
                            .border(
                                border = BorderStroke(2.dp, Color(LocalContext.current.getColor(R.color.gold))),
                                shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CreateList(databaseManager) { list ->
                                val intent = Intent(this@GenerateList, SpinTheWheel::class.java)
                                intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(list))
                                intent.putExtra("IS_USER_GENERATED", true)
                                startActivity(intent)
                            }
                        }
                          Row(
                              modifier = Modifier
                                  .fillMaxWidth()
                                  .align(Alignment.CenterHorizontally)
                                  .padding(vertical = 8.dp),
                              horizontalArrangement = Arrangement.Center
                          ) {
                              ExpandableCardList(this@GenerateList, databaseManager)
                          }
                    }
                  }
              )
            }
        }
    }
}

@Composable //Creating list composable, will include text fields
fun CreateList(db : DatabaseManager, onListComplete: (List<String>) -> Unit) {
    var numberOfItems by remember { mutableStateOf("") }//number of items within a list
    var itemsCount by remember { mutableIntStateOf(0) } //how many items
    val items = remember { mutableStateListOf<String>() } //items themselves
    var newItem by remember { mutableStateOf("") } //get the item from the text field
    var listName by remember { mutableStateOf("") } //get the name of the list
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            .background(Color(LocalContext.current.getColor(R.color.dark_blue_custom)))
    ) {
        //Title of the list activity
        Text(
            text = "Generate a list below!",
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally),
            color = Color(LocalContext.current.getColor(R.color.off_white)),
            style = MaterialTheme.typography.titleLarge
        )
        //Adding the items list size
        TextField(
            value = listName,
            onValueChange = { input->
                listName = input
            },
            label = { Text("Enter your list's name:" ) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = numberOfItems,
            onValueChange = { input ->
                numberOfItems = input
                itemsCount = input.toIntOrNull() ?: 0
            },
            label = { Text("Enter Number of Items") },
            modifier = Modifier.fillMaxWidth(),
        )
        //Keeping track of what items are left to add
        Text(
            text = "Number of items left to create: $itemsCount",
            color = Color(LocalContext.current.getColor(ofF_white)),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        //if we haven't reached the limit we keep adding
        if (items.size < itemsCount) {
            TextField(
                value = newItem,
                onValueChange = { newItem = it },
                label = { Text("Add Item") },
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth(),
                enabled = items.size < itemsCount
            ) {
                Text("Add Item")
            }

            Text(
                text = "Items added: ${items.size} / $itemsCount",
                color = Color(LocalContext.current.getColor(ofF_white)),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else if (items.size == itemsCount && itemsCount > 0) {
            //if the limit was reached we can stop the adding and complete the list
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                    contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                ),
                onClick = {
                    addToDatabase(listName, items.toList(), db)
                    onListComplete(items.toList())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete List")
            }
        }

        //Show the user what kind of list they created
        Spacer(modifier = Modifier.height(16.dp))
        Text("Current List:",
            color = Color(LocalContext.current.getColor(ofF_white)),
            style = MaterialTheme.typography.bodyLarge)

        items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                TextField(
                    value = item,
                    onValueChange = { updatedValue -> items[index] = updatedValue },
                    label = { Text("Item ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                //Give the user the option to remove an item from the list
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                        contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                    ),
                    onClick = { items.removeAt(index) },//removes from the list
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

fun addToDatabase(name : String, list : List<String>, db:DatabaseManager){
    val stringList = list.joinToString(":")
    Log.i("ListString", stringList)
    db.addList(name, stringList) //add to database :3
}


@Composable
fun ShowPreviousLists(db : DatabaseManager, onListComplete: (List<String>) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        db.getAllLists().forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        color = Color(LocalContext.current.getColor(R.color.brown)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                )  {
                    Text(
                        text = "List Name: ${item.first}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(LocalContext.current.getColor(R.color.off_white))
                    )
                    Text(
                        text = "Items: ${item.second}",
                        fontSize = 12.sp,
                        color = Color(LocalContext.current.getColor(R.color.off_white))
                    )
                }
                //Align to the end (the right)
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(LocalContext.current.getColor(R.color.gold)),
                            contentColor = Color(LocalContext.current.getColor(R.color.off_white))
                        ),
                        onClick = {
                            //separate the list based on : and add to list
                            onListComplete(item.second.split(":"))
                        },//removes from the list
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                    ) {
                        Text("X")
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}
@Composable
fun ExpandableCard(title: String, context : Context, db: DatabaseManager) {
    //to track the expansion
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor =Color(LocalContext.current.getColor(gold)),
            contentColor = Color(LocalContext.current.getColor(ofF_white))
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(), //Animation for expanding
                exit = shrinkVertically() //Animation for collapse
            ) {
                ShowPreviousLists(db = db) { list ->
                    val intent = Intent(context, SpinTheWheel::class.java)
                    intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(list))
                    intent.putExtra("IS_USER_GENERATED", true)
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun ExpandableCardList(context: Context, db : DatabaseManager) {
    Column(modifier = Modifier.fillMaxSize()) {
        ExpandableCard(
            title = "Check out your previous lists",
            context = context,
            db = db
        )
    }
}
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