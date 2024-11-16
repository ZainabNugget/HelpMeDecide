package com.griffith.helpmedecide

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class GenerateList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //Will switch the intent to the spin the wheel activity
            CreateList { list ->
                val intent = Intent(this, SpinTheWheel::class.java)
                intent.putStringArrayListExtra("ITEMS_LIST", ArrayList(list))
                intent.putExtra("IS_USER_GENERATED", true)
                startActivity(intent)
            }
        }
    }
}

@Composable //Creating list composable, will include trextfeilds
fun CreateList(onListComplete: (List<String>) -> Unit) {
    //number of items within a list
    var numberOfItems by remember { mutableStateOf("") }
    var itemsCount by remember { mutableIntStateOf(0) } //how many items
    val items = remember { mutableStateListOf<String>() } //items themselves
    var newItem by remember { mutableStateOf("") } //get the item from the textfeild

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center //vertically aligned
    ) {
        //Title of the list activity
        Text(
            text = "Generate a list below!",
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleLarge
        )
        //Adding the items list size
        TextField(
            value = numberOfItems,
            onValueChange = { input ->
                numberOfItems = input
                itemsCount = input.toIntOrNull() ?: 0
            },
            label = { Text("Enter Number of Items") },
            modifier = Modifier.fillMaxWidth()
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
                onClick = { onListComplete(items.toList()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                // TODO(Implement a way to take this into the spin the wheel acitivyt)
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
                //Giver the user the option to remove an item from the list
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


@Preview (showBackground = true)
@Composable
fun PreviewList(){
    CreateList {  }
}