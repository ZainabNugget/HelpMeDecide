package com.griffith.helpmedecide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class SpinTheWheel : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SpinWheelDraw()
            }
        }
    }
}

@Composable
fun SpinWheelDraw() {
    var isSpinning by remember { mutableStateOf(false) }
    var currentRotation by remember { mutableStateOf(0f) }
    var targetRotation by remember { mutableStateOf(0f) }
    val segments = listOf("A", "B", "C", "D", "E", "F","G","H") //list of items
    // Smoothly animate to the target rotation
    val animatedRotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(
            durationMillis = 4000,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            isSpinning = false
            currentRotation = targetRotation % 360
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DrawTheWheel(rotationAngle = animatedRotation, segments = segments)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isSpinning) {
                    val randomEndAngle = Random.nextInt(0, 360)
                    val fullRotations = 5 * 360
                    targetRotation = currentRotation + fullRotations + randomEndAngle
                    isSpinning = true
                }
            }
        ) {
            Text(text = "Spin the Wheel")
        }
    }
}

@Composable
fun DrawTheWheel(rotationAngle: Float, segments : List<String>) {
//    val segments = listOf("A", "B", "C", "D", "E", "F","G","H") //list of items
    val colors = listOf(Color.DarkGray, Color.LightGray)
    Box{
        Canvas(modifier = Modifier.size(300.dp)) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2
            val center = Offset(size.width / 2, size.height / 2)
            val segmentAngle = 360f / segments.size
            rotate(degrees = rotationAngle, pivot = center) {
                for (i in segments.indices) {
                    val color = colors[i % 2]
                    drawArc(
                        color = color,
                        startAngle = i * segmentAngle,
                        sweepAngle = segmentAngle,
                        useCenter = true,
                        size = Size(canvasSize, canvasSize),
                        topLeft = Offset((size.width - canvasSize) / 2, (size.height - canvasSize) / 2)
                    )

                    val textAngle = (i * segmentAngle + segmentAngle / 2)
                    val textRadius = radius * 0.7f
                    val x = center.x + textRadius * Math.cos(Math.toRadians(textAngle.toDouble())).toFloat()
                    val y = center.y + textRadius * Math.sin(Math.toRadians(textAngle.toDouble())).toFloat()

                    drawContext.canvas.nativeCanvas.apply {
                        save()
                        //rotate(textAngle)
                        drawText(
                            segments[i],
                            x,
                            y,
                            android.graphics.Paint().apply {
                                textSize = 50f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                        restore()
                    }
                }
            }
            //circle in the middle
            drawCircle(color = Color.White, radius = radius * 0.1f, center = center)
            //Little trianlge thing
            val trianglePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(center.x - 15f, center.y - radius - 20f)
                lineTo(center.x + 15f, center.y - radius - 20f)
                lineTo(center.x, center.y - radius + 40f)
                close()
            }
            drawPath(path = trianglePath, color = Color.Red)
        }
    }

}
//To debug and stuff
@Preview(showBackground = true)
@Composable
fun PreviewSpinWheelApp() {
    SpinWheelDraw()
}
