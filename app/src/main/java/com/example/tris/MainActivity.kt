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
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tris.ui.theme.TrisTheme
import kotlin.random.Random

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

enum class GameMode {
    Single, BestOf3
}

enum class Player {
    X, O
}

@Composable
fun TrisApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var selectedMode by remember { mutableStateOf(GameMode.Single) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            Screen.Home -> HomeScreen(
                onStartGame = { mode ->
                    selectedMode = mode
                    currentScreen = Screen.Game
                },
                modifier = Modifier.padding(innerPadding),
            )
            Screen.Game -> GameScreen(
                gameMode = selectedMode,
                onBackToHome = { currentScreen = Screen.Home },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun HomeScreen(onStartGame: (GameMode) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        TrisBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tris Game",
                fontSize = 48.sp,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { onStartGame(GameMode.Single) },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(text = "Single Match", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onStartGame(GameMode.BestOf3) },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(text = "Best of 3", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun TrisBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val color = Color.Gray.copy(alpha = 0.05f)
        val strokeWidth = 8.dp.toPx()

        // Draw a large faint grid
        val gridSize = size.minDimension * 0.8f
        val startX = (size.width - gridSize) / 2
        val startY = (size.height - gridSize) / 2

        for (i in 1..2) {
            // Vertical lines
            drawLine(
                color = color,
                start = Offset(startX + i * gridSize / 3, startY),
                end = Offset(startX + i * gridSize / 3, startY + gridSize),
                strokeWidth = strokeWidth / 2
            )
            // Horizontal lines
            drawLine(
                color = color,
                start = Offset(startX, startY + i * gridSize / 3),
                end = Offset(startX + gridSize, startY + i * gridSize / 3),
                strokeWidth = strokeWidth / 2
            )
        }

        // Draw some random faint symbols
        val symbolSize = 60.dp.toPx()
        
        // Top Left O
        drawCircle(
            color = color,
            radius = symbolSize / 2,
            center = Offset(size.width * 0.15f, size.height * 0.15f),
            style = Stroke(width = strokeWidth)
        )

        // Bottom Right X
        val xCenter = Offset(size.width * 0.85f, size.height * 0.85f)
        drawLine(
            color = color,
            start = Offset(xCenter.x - symbolSize / 2, xCenter.y - symbolSize / 2),
            end = Offset(xCenter.x + symbolSize / 2, xCenter.y + symbolSize / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(xCenter.x + symbolSize / 2, xCenter.y - symbolSize / 2),
            end = Offset(xCenter.x - symbolSize / 2, xCenter.y + symbolSize / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        // Mid Left X
        val xCenter2 = Offset(size.width * 0.1f, size.height * 0.6f)
        drawLine(
            color = color,
            start = Offset(xCenter2.x - symbolSize / 3, xCenter2.y - symbolSize / 3),
            end = Offset(xCenter2.x + symbolSize / 3, xCenter2.y + symbolSize / 3),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(xCenter2.x + symbolSize / 3, xCenter2.y - symbolSize / 3),
            end = Offset(xCenter2.x - symbolSize / 3, xCenter2.y + symbolSize / 3),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Mid Right O
        drawCircle(
            color = color,
            radius = symbolSize / 3,
            center = Offset(size.width * 0.9f, size.height * 0.4f),
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun GameScreen(gameMode: GameMode, onBackToHome: () -> Unit, modifier: Modifier = Modifier) {
    var scoreO by remember { mutableIntStateOf(0) }
    var scoreX by remember { mutableIntStateOf(0) }
    var startingPlayer by remember { mutableStateOf(if (Random.nextBoolean()) Player.O else Player.X) }

    var board by remember { mutableStateOf(List(9) { null as Player? }) }
    var currentPlayer by remember { mutableStateOf(startingPlayer) }
    var winningPattern by remember { mutableStateOf(null as List<Int>?) }
    var roundWinner by remember { mutableStateOf(null as Player?) }
    var matchWinner by remember { mutableStateOf(null as Player?) }
    var isDraw by remember { mutableStateOf(false) }

    val onCellClick: (Int) -> Unit = { index ->
        if (board[index] == null && roundWinner == null && matchWinner == null && !isDraw) {
            val newBoard = board.toMutableList()
            newBoard[index] = currentPlayer
            board = newBoard

            val pattern = getWinningPattern(newBoard)
            if (pattern != null) {
                winningPattern = pattern
                val result = newBoard[pattern[0]]!!
                roundWinner = result
                if (result == Player.O) scoreO++ else scoreX++

                if (gameMode == GameMode.Single) {
                    matchWinner = result
                } else if (scoreO == 2 || scoreX == 2) {
                    matchWinner = result
                }
            } else if (newBoard.none { it == null }) {
                isDraw = true
            } else {
                currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
            }
        }
    }

    val resetRound = {
        board = List(9) { null }
        val nextStart = if (Random.nextBoolean()) Player.O else Player.X
        startingPlayer = nextStart
        currentPlayer = nextStart
        winningPattern = null
        roundWinner = null
        isDraw = false
        if (matchWinner != null) {
            matchWinner = null
            scoreO = 0
            scoreX = 0
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gameMode == GameMode.BestOf3) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreText(label = "Circle", score = scoreO, color = Color.Blue)
                ScoreText(label = "Cross", score = scoreX, color = Color.Red)
            }
        }

        Text(
            text = when {
                matchWinner != null -> "Player ${if (matchWinner == Player.O) "Circle" else "Cross"} wins the match!"
                roundWinner != null -> "Player ${if (roundWinner == Player.O) "Circle" else "Cross"} wins the round!"
                isDraw -> "It's a Draw!"
                else -> "Player ${if (currentPlayer == Player.O) "Circle" else "Cross"}'s Turn"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Board(board, winningPattern, onCellClick)

        Spacer(modifier = Modifier.height(48.dp))

        if (matchWinner != null || isDraw) {
            Button(onClick = resetRound, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Play Again")
            }
            Spacer(modifier = Modifier.height(8.dp))
        } else if (roundWinner != null) {
            Button(onClick = resetRound, modifier = Modifier.fillMaxWidth(0.6f)) {
                Text("Next Round")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth(0.6f)) {
            Text("Back")
        }
    }
}

@Composable
fun ScoreText(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(text = score.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun Board(board: List<Player?>, winningPattern: List<Int>?, onCellClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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

        if (winningPattern != null) {
            val winningPlayer = board[winningPattern[0]]
            val lineColor = when (winningPlayer) {
                Player.X -> Color.Red
                Player.O -> Color.Blue
                else -> Color.Black
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / 3
                val startRow = winningPattern[0] / 3
                val startCol = winningPattern[0] % 3
                val endRow = winningPattern[2] / 3
                val endCol = winningPattern[2] % 3

                drawLine(
                    color = lineColor,
                    start = Offset(
                        x = startCol * cellSize + cellSize / 2,
                        y = startRow * cellSize + cellSize / 2
                    ),
                    end = Offset(
                        x = endCol * cellSize + cellSize / 2,
                        y = endRow * cellSize + cellSize / 2
                    ),
                    strokeWidth = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
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

fun getWinningPattern(board: List<Player?>): List<Int>? {
    val winPatterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
        listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
    )

    for (pattern in winPatterns) {
        val (a, b, c) = pattern
        if (board[a] != null && board[a] == board[b] && board[a] == board[c]) {
            return pattern
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
