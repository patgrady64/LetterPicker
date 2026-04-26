package com.pgdevhouse.letterpicker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TreasureHuntApp() } }
    }
}

@Composable
fun TreasureHuntApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("hunt_data", Context.MODE_PRIVATE) }

    // --- GAME STATES ---
    var currentLetter by remember { mutableStateOf("?") }
    var lastLetter by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf(Color(0xFFF0F0F0)) }
    var connorScore by remember { mutableStateOf(prefs.getInt("connor", 0)) }
    var maxScore by remember { mutableStateOf(prefs.getInt("max", 0)) }

    // This is our "Deck" of letters that haven't been used yet
    var availableLetters by remember { mutableStateOf(('A'..'Z').shuffled()) }

    var history by remember {
        val saved = prefs.getStringSet("history", emptySet()) ?: emptySet()
        mutableStateOf(saved.toList().sortedDescending())
    }
    var showHistory by remember { mutableStateOf(false) }

    // --- FUNCTIONS ---
    fun pickNextLetter() {
        // If we ran out of letters, reshuffle a new deck
        val currentDeck = if (availableLetters.isEmpty()) {
            ('A'..'Z').shuffled()
        } else {
            availableLetters
        }

        val next = currentDeck.first().toString()

        if (currentLetter != "?") lastLetter = currentLetter
        currentLetter = next

        // Remove the letter we just picked from the deck
        availableLetters = currentDeck.drop(1)

        backgroundColor = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
    }

    fun archiveGame() {
        val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        val entry = "$date - Connor: $connorScore | Max: $maxScore"
        val newSet = (prefs.getStringSet("history", emptySet()) ?: emptySet()).toMutableSet().apply { add(entry) }
        prefs.edit().putStringSet("history", newSet).putInt("connor", 0).putInt("max", 0).apply()

        history = newSet.toList().sortedDescending()
        connorScore = 0
        maxScore = 0
        currentLetter = "?"
        lastLetter = ""
        availableLetters = ('A'..'Z').shuffled() // Reset the deck
        backgroundColor = Color(0xFFF0F0F0)
    }

    if (showHistory) {
        // --- HISTORY VIEW ---
        Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
            Text("History", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(history) { record ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(record, fontSize = 16.sp)
                        IconButton(onClick = {
                            val newSet = (prefs.getStringSet("history", emptySet()) ?: emptySet()).toMutableSet().apply { remove(record) }
                            prefs.edit().putStringSet("history", newSet).apply()
                            history = newSet.toList().sortedDescending()
                        }) { Text("🗑️") }
                    }
                    HorizontalDivider()
                }
            }
            Button(onClick = { showHistory = false }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
            IconButton(onClick = { showHistory = true }, Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 10.dp)) {
                Text("📜", fontSize = 32.sp)
            }

            // --- SCOREBOARD ---
            Row(Modifier.fillMaxWidth().padding(top = 80.dp), Arrangement.SpaceEvenly) {
                ScoreControl("Connor", connorScore) {
                    connorScore++
                    prefs.edit().putInt("connor", connorScore).apply()
                }
                ScoreControl("Max", maxScore) {
                    maxScore++
                    prefs.edit().putInt("max", maxScore).apply()
                }
            }

            // --- MAIN LETTER AREA ---
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                if (lastLetter.isNotEmpty()) {
                    Text("Previous: $lastLetter", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                AnimatedContent(
                    targetState = currentLetter,
                    transitionSpec = { (scaleIn(animationSpec = spring(0.5f, 400f)) + fadeIn()).togetherWith(scaleOut() + fadeOut()) },
                    label = ""
                ) { target ->
                    Text(text = target, fontSize = 300.sp, fontWeight = FontWeight.Black)
                }

                // Progress counter so you know how many letters are left
                Text("Letters remaining: ${availableLetters.size}", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.5f))
            }

            // --- BUTTONS ---
            Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { pickNextLetter() },
                    modifier = Modifier.height(75.dp).fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("PICK NEXT LETTER", fontSize = 22.sp)
                }
                OutlinedButton(onClick = { archiveGame() }, modifier = Modifier.fillMaxWidth(0.6f)) {
                    Text("SAVE & NEW GAME", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ScoreControl(name: String, score: Int, onIncrease: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("$score", fontSize = 40.sp, fontWeight = FontWeight.Black)
        Button(onClick = onIncrease, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
            Text("+", fontSize = 24.sp)
        }
    }
}