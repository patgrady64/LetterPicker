package com.pgdevhouse.letterpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FullScreenLetterApp()
        }
    }
}

@Composable
fun FullScreenLetterApp() {
    // State for the letter and the background color
    var currentLetter by remember { mutableStateOf("?") }
    var backgroundColor by remember { mutableStateOf(Color.White) }

    // This function generates a random bright-ish color
    fun getRandomColor(): Color {
        return Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // This fills the whole screen with color
    ) {
        // The Giant Letter
        Text(
            text = currentLetter,
            fontSize = 300.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black // Keep letter black so it stands out against colors
        )

        // The Bottom Button
        Button(
            onClick = {
                currentLetter = ('A'..'Z').random().toString()
                backgroundColor = getRandomColor()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .height(60.dp)
                .width(220.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("NEXT LETTER", fontSize = 20.sp, color = Color.White)
        }
    }
}