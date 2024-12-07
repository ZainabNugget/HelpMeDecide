@file:OptIn(ExperimentalLayoutApi::class)

package com.griffith.helpmedecide

/*
* Name: Zainab Wadullah
* BSCH - Stage 4 MD
* Student Number: 3088942
* */

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ofF_white : Int = R.color.off_white

class GenerateList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val databaseManager = DatabaseManager(this)
            val allData = databaseManager.getAllLists()
            allData.forEach { Log.i("Database", it.toString()) }

            val backGroundColor : Int = LocalContext.current.getColor(R.color.dark_blue_custom)
            //Will switch the intent to the spin the wheel activity
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .background(Color(backGroundColor))
            ) {
                CreateList(databaseManager) { list ->
                    val intent = Intent(this@GenerateList, SpinTheWheel::class.java)
                    intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(list))
                    intent.putExtra("IS_USER_GENERATED", true)
                    startActivity(intent)
                }
                ShowPreviousLists(db = databaseManager)
            }

        }
    }
}

@Composable //Creating list composable, will include trextfeilds
fun CreateList(db : DatabaseManager, onListComplete: (List<String>) -> Unit) {
    var numberOfItems by remember { mutableStateOf("") }//number of items within a list
    var itemsCount by remember { mutableIntStateOf(0) } //how many items
    val items = remember { mutableStateListOf<String>() } //items themselves
    var newItem by remember { mutableStateOf("") } //get the item from the textfeild
    var listName by remember { mutableStateOf("") } //get the name of the list
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        //Title of the list activity
        Text(
            text = "Generate a list below!",
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally),
            color = Color(ofF_white),
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
            modifier = Modifier.padding(vertical = 8.dp)
        )
        //if we havent reached the limit we keep adding
        if (items.size < itemsCount) {
            TextField(
                value = newItem,
                onValueChange = { newItem = it },
                label = { Text("Add Item") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else if (items.size == itemsCount && itemsCount > 0) {
            //if the limit was reached we can stop the adding and complete the list
            Button(
                onClick = {
                    AddToDatabase(listName, items.toList(), db)
                    onListComplete(items.toList())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete List")
            }
        }

        //Show the user what kind of list they created
        Spacer(modifier = Modifier.height(16.dp))
        Text("Current List:", style = MaterialTheme.typography.bodyLarge)

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

fun AddToDatabase(name : String, list : List<String>, db:DatabaseManager){
    val list_to_string = list.joinToString(":")
    Log.i("ListString", list_to_string)
    db.addList(name, list_to_string) //add to database :3
}


@Composable
fun ShowPreviousLists(db : DatabaseManager){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title Row
        Text(
            text = "Check out your previous lists:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(ofF_white),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp) // Add spacing below the title
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display the list of items
        db.getAllLists().forEach { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp) // Add vertical spacing between items
                    .background(
                        color = Color(0xFFF1F1F1), // Light gray background for each list item
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp) // Padding inside the background
            ) {
                Text(
                    text = "List Name: ${item.first}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "Items: ${item.second}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Add spacing between each list block
        }
    }

}

@Preview (showBackground = true)
@Composable
fun PreviewLists(){
    val db : DatabaseManager = DatabaseManager(LocalContext.current)
    ShowPreviousLists(db)
}

//@Preview (showBackground = true)
//@Composable
//fun PreviewList(){
//    CreateList {  }
//}