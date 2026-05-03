package com.example.tris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tris.ui.theme.TrisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrisTheme {
                TrisApp()
            }
        }
    }
}

enum class Screen {
    Home, Game
}

enum class Player {
    X, O
}

@Composable
fun TrisApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            Screen.Home -> HomeScreen(
                onStartGame = { currentScreen = Screen.Game },
                modifier = Modifier.padding(innerPadding),
            )
            Screen.Game -> GameScreen(
                onBackToHome = { currentScreen = Screen.Home },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun HomeScreen(onStartGame: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tris Game",
            fontSize = 48.sp,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStartGame) {
            Text(text = "Start Game", fontSize = 20.sp)
        }
    }
}

@Composable
fun GameScreen(onBackToHome: () -> Unit, modifier: Modifier = Modifier) {
    var board by remember { mutableStateOf(List(9) { null as Player? }) }
    var currentPlayer by remember { mutableStateOf(Player.O) } // Circle first as per request
    var winner by remember { mutableStateOf(null as Player?) }
    var isDraw by remember { mutableStateOf(false) }

    val onCellClick: (Int) -> Unit = { index ->
        if (board[index] == null && winner == null && !isDraw) {
            val newBoard = board.toMutableList()
            newBoard[index] = currentPlayer
            board = newBoard

            winner = checkWinner(newBoard)
            if (winner == null) {
                if (newBoard.none { it == null }) {
                    isDraw = true
                } else {
                    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
                }
            }
        }
    }

    val resetGame = {
        board = List(9) { null }
        currentPlayer = Player.O
        winner = null
        isDraw = false
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                winner != null -> "Player ${if (winner == Player.O) "Circle" else "Cross"} Wins!"
                isDraw -> "It's a Draw!"
                else -> "Player ${if (currentPlayer == Player.O) "Circle" else "Cross"}'s Turn"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Board(board, onCellClick)

        Spacer(modifier = Modifier.height(48.dp))

        if (winner != null || isDraw) {
            Button(onClick = resetGame, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Play Again")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth(0.6f)) {
            Text("Back")
        }
    }
}

@Composable
fun Board(board: List<Player?>, onCellClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, Color.Black)
    ) {
        for (row in 0 until 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    Cell(
                        player = board[index],
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun Cell(player: Player?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, Color.Gray)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (player) {
                Player.X -> "X"
                Player.O -> "O"
                null -> ""
            },
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = when (player) {
                Player.X -> Color.Red
                Player.O -> Color.Blue
                null -> Color.Unspecified
            }
        )
    }
}

fun checkWinner(board: List<Player?>): Player? {
    val winPatterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
        listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
    )

    for (pattern in winPatterns) {
        val (a, b, c) = pattern
        if (board[a] != null && board[a] == board[b] && board[a] == board[c]) {
            return board[a]
        }
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrisTheme {
        TrisApp()
    }
}
