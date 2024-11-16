package com.griffith.helpmedecide

import android.os.Bundle
import android.widget.Space
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

        }
    }
}

@Composable
fun NumberOfItems() {
    var numberOfItems by remember { mutableStateOf("") } // Use String for TextField
    var itemsCount by remember { mutableStateOf(0) } // Store the actual Int value

    Column {
        TextField(
            value = numberOfItems,
            onValueChange = { input ->
                numberOfItems = input // Update the text in the TextField
                itemsCount = input.toIntOrNull() ?: 0 // Convert to Int, default to 0 for invalid input
            },
            label = { Text("Enter Number of Items") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "You entered: $itemsCount items", modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun CreateList(onListComplete: (List<String>) -> Unit) {
    var numberOfItems by remember { mutableStateOf("") }
    var itemsCount by remember { mutableStateOf(0) }
    val items = remember { mutableStateListOf<String>() }
    var newItem by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Generate a list below!",
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleLarge
        )
        TextField(
            value = numberOfItems,
            onValueChange = { input ->
                numberOfItems = input
                itemsCount = input.toIntOrNull() ?: 0
            },
            label = { Text("Enter Number of Items") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Number of items to create: $itemsCount",
            modifier = Modifier.padding(vertical = 8.dp)
        )

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
        }

        if (items.size == itemsCount && itemsCount > 0) {
            Button(
                onClick = { onListComplete(items.toList()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete List")
            }
        }

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
                Button(
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


@Preview (showBackground = true)
@Composable
fun PreviewList(){
    CreateList {  }
}