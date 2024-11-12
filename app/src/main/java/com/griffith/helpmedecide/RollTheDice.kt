package com.griffith.helpmedecide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class RollTheDice : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(){
            Surface (
                modifier = Modifier.fillMaxSize().background(color = Color.Black)
            )
            {
                DrawTheDice()
            }
        }
    }
}

@Composable
fun DrawTheDice(){
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasSize = size.minDimension / 2f
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        drawRect(
            color = Color.White,
            size = androidx.compose.ui.geometry.Size(canvasSize, canvasSize),
            topLeft = Offset(centerX - canvasSize / 2, centerY - canvasSize / 2)
        )

        val dotRadius = 30f
        drawCircle(
            color = Color.Black,
            radius = dotRadius,
            center = Offset(centerX, centerY)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDiceCanvas() {
    MaterialTheme {
        DrawTheDice()
    }
}