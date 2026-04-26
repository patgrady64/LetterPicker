package com.pgdevhouse.letterpicker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
        setContent {
            MaterialTheme {
                TreasureHuntApp()
            }
        }
    }
}

@Composable
fun TreasureHuntApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("hunt_data", Context.MODE_PRIVATE) }

    // --- GAME STATES ---
    var currentLetter by remember { mutableStateOf("?") }
    var backgroundColor by remember { mutableStateOf(Color(0xFFF0F0F0)) }
    var connorScore by remember { mutableStateOf(prefs.getInt("connor", 0)) }
    var maxScore by remember { mutableStateOf(prefs.getInt("max", 0)) }

    var history by remember {
        val savedHistory = prefs.getStringSet("history", emptySet()) ?: emptySet()
        mutableStateOf(savedHistory.toList().sortedDescending())
    }
    var showHistory by remember { mutableStateOf(false) }

    // --- LOGIC FUNCTIONS ---
    fun saveScoresToDisk() {
        prefs.edit().putInt("connor", connorScore).putInt("max", maxScore).apply()
    }

    fun archiveGame() {
        val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        val entry = "$date - Connor: $connorScore | Max: $maxScore"
        val currentSet = prefs.getStringSet("history", emptySet()) ?: emptySet()
        val newSet = currentSet.toMutableSet()
        newSet.add(entry)

        prefs.edit().putStringSet("history", newSet).putInt("connor", 0).putInt("max", 0).apply()
        history = newSet.toList().sortedDescending()
        connorScore = 0
        maxScore = 0
        currentLetter = "?"
        backgroundColor = Color(0xFFF0F0F0)
    }

    fun deleteHistoryItem(item: String) {
        val currentSet = prefs.getStringSet("history", emptySet()) ?: emptySet()
        val newSet = currentSet.toMutableSet()
        newSet.remove(item)
        prefs.edit().putStringSet("history", newSet).apply()
        history = newSet.toList().sortedDescending()
    }

    fun deleteAllHistory() {
        prefs.edit().remove("history").apply()
        history = emptyList()
    }

    if (showHistory) {
        // --- HISTORY VIEW ---
        Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("History", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                TextButton(onClick = { deleteAllHistory() }) {
                    Text("Clear All", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(history) { record ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(record, fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
                        IconButton(onClick = { deleteHistoryItem(record) }) {
                            Text("❌", fontSize = 18.sp) // Simple delete button
                        }
                    }
                    HorizontalDivider(thickness = 1.dp)
                }
            }

            Button(
                onClick = { showHistory = false },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Back to Game", color = Color.White)
            }
        }
    } else {
        // --- MAIN GAME UI ---
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
            IconButton(
                onClick = { showHistory = true },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 10.dp)
            ) {
                Text("📜", fontSize = 32.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreControl("Connor", connorScore) { connorScore++; saveScoresToDisk() }
                Box(modifier = Modifier.width(1.dp).height(60.dp).background(Color.Black.copy(0.2f)))
                ScoreControl("Max", maxScore) { maxScore++; saveScoresToDisk() }
            }

            Text(
                text = currentLetter,
                fontSize = 280.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )

            Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        currentLetter = ('A'..'Z').random().toString()
                        backgroundColor = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
                    },
                    modifier = Modifier.height(75.dp).fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("PICK NEXT LETTER", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { if (connorScore > 0 || maxScore > 0) archiveGame() },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                ) {
                    Text("END & SAVE GAME", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ScoreControl(name: String, score: Int, onIncrease: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$name: $score", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        Button(
            onClick = onIncrease,
            modifier = Modifier.padding(top = 4.dp).size(width = 80.dp, height = 45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("+", fontSize = 24.sp, color = Color.White)
        }
    }
}