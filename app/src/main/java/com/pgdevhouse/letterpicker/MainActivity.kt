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
            // MaterialTheme ensures the app uses standard Android styling
            MaterialTheme {
                TreasureHuntApp()
            }
        }
    }
}

@Composable
fun TreasureHuntApp() {
    // --- GAME STATE ---
    var currentLetter by remember { mutableStateOf("?") }
    var backgroundColor by remember { mutableStateOf(Color(0xFFF0F0F0)) }

    // --- SCORE STATE ---
    var connorScore by remember { mutableIntStateOf(0) }
    var maxScore by remember { mutableIntStateOf(0) }

    // Helper to get a random background color
    fun getRandomColor(): Color {
        return Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- SCOREBOARD (Top of screen) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreControl(name = "Connor", score = connorScore, onIncrease = { connorScore++ })

            // Visual vertical divider
            Box(modifier = Modifier.width(2.dp).height(40.dp).background(Color.Black.copy(alpha = 0.3f)))

            ScoreControl(name = "Max", score = maxScore, onIncrease = { maxScore++ })
        }

        // --- THE GIANT LETTER (Center) ---
        Text(
            text = currentLetter,
            fontSize = 300.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black
        )

        // --- BUTTON GROUP (Bottom) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Action Button
            Button(
                onClick = {
                    currentLetter = ('A'..'Z').random().toString()
                    backgroundColor = getRandomColor()
                },
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("PICK NEXT LETTER", fontSize = 22.sp, color = Color.White)
            }

            // Secondary Reset Button
            OutlinedButton(
                onClick = {
                    currentLetter = "?"
                    connorScore = 0
                    maxScore = 0
                    backgroundColor = Color(0xFFF0F0F0)
                },
                modifier = Modifier.fillMaxWidth(0.5f),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
            ) {
                Text("START NEW GAME", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScoreControl(name: String, score: Int, onIncrease: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$name: $score",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Button(
            onClick = onIncrease,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(width = 70.dp, height = 45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("+", fontSize = 28.sp, color = Color.White)
        }
    }
}