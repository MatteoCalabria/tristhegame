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
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay
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

enum class OpponentType {
    Human, AI
}

enum class GameMode {
    Single, BestOf3, BattleRoyale
}

enum class Player {
    X, O
}

@Composable
fun TrisApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var selectedOpponent by remember { mutableStateOf(OpponentType.Human) }
    var selectedMode by remember { mutableStateOf(GameMode.Single) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            Screen.Home -> HomeScreen(
                onStartGame = { opponent, mode ->
                    selectedOpponent = opponent
                    selectedMode = mode
                    currentScreen = Screen.Game
                },
                modifier = Modifier.padding(innerPadding),
            )
            Screen.Game -> GameScreen(
                opponentType = selectedOpponent,
                gameMode = selectedMode,
                onBackToHome = { currentScreen = Screen.Home },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun HomeScreen(onStartGame: (OpponentType, GameMode) -> Unit, modifier: Modifier = Modifier) {
    var step by remember { mutableStateOf(1) }
    var selectedOpponent by remember { mutableStateOf(OpponentType.Human) }

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

            if (step == 1) {
                Button(
                    onClick = {
                        selectedOpponent = OpponentType.Human
                        step = 2
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = "Human vs Human", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedOpponent = OpponentType.AI
                        step = 2
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = "Human vs AI", fontSize = 20.sp)
                }
            } else {
                Button(
                    onClick = { onStartGame(selectedOpponent, GameMode.Single) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = "Single Match", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onStartGame(selectedOpponent, GameMode.BestOf3) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = "Best of 3", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onStartGame(selectedOpponent, GameMode.BattleRoyale) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(text = "Battle Royale", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { step = 1 },
                    modifier = Modifier.fillMaxWidth(0.4f)
                ) {
                    Text(text = "Back")
                }
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
fun GameScreen(opponentType: OpponentType, gameMode: GameMode, onBackToHome: () -> Unit, modifier: Modifier = Modifier) {
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
            // In Versus AI, only allow clicking if it's Player O's turn
            if (opponentType != OpponentType.AI || currentPlayer == Player.O) {
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
                    } else if (gameMode == GameMode.BestOf3 && (scoreO == 2 || scoreX == 2)) {
                        matchWinner = result
                    }
                    // Battle Royale never sets matchWinner
                } else if (newBoard.none { it == null }) {
                    isDraw = true
                } else {
                    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
                }
            }
        }
    }

    // AI Logic
    LaunchedEffect(currentPlayer, roundWinner, matchWinner, isDraw) {
        if (opponentType == OpponentType.AI && currentPlayer == Player.X && roundWinner == null && matchWinner == null && !isDraw) {
            delay(600) // Small delay for better UX
            val aiMove = getAiMove(board, Player.X, Player.O)
            if (aiMove != -1) {
                val newBoard = board.toMutableList()
                newBoard[aiMove] = Player.X
                board = newBoard

                val pattern = getWinningPattern(newBoard)
                if (pattern != null) {
                    winningPattern = pattern
                    roundWinner = Player.X
                    scoreX++
                    if (gameMode == GameMode.Single) {
                        matchWinner = Player.X
                    } else if (gameMode == GameMode.BestOf3 && (scoreO == 2 || scoreX == 2)) {
                        matchWinner = Player.X
                    }
                } else if (newBoard.none { it == null }) {
                    isDraw = true
                } else {
                    currentPlayer = Player.O
                }
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

    Box(modifier = modifier.fillMaxSize()) {
        GameBackground(opponentType)
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (gameMode != GameMode.Single) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreText(label = "Circle", score = scoreO, color = Color.Blue)
                    ScoreText(label = "Cross", score = scoreX, color = Color.Red)
                }
            }

            val resultText = when {
                matchWinner != null -> "Player ${if (matchWinner == Player.O) "Circle" else "Cross"} wins the match!"
                roundWinner != null -> {
                    val winnerName = if (roundWinner == Player.O) "Circle" else "Cross"
                    if (gameMode == GameMode.BattleRoyale) {
                        val battleStatus = when {
                            scoreO > scoreX -> "Player Circle is winning the battle"
                            scoreX > scoreO -> "Player Cross is winning the battle"
                            else -> "The battle hangs in the balance"
                        }
                        "Player $winnerName wins the round. $battleStatus"
                    } else {
                        "Player $winnerName wins the round!"
                    }
                }
                isDraw -> "It's a Draw!"
                else -> "Player ${if (currentPlayer == Player.O) "Circle" else "Cross"}'s Turn"
            }

            Text(
                text = resultText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
}

@Composable
fun GameBackground(opponentType: OpponentType) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val color = Color.Gray.copy(alpha = 0.05f)
        val strokeWidth = 4.dp.toPx()

        if (opponentType == OpponentType.Human) {
            // Draw two human icons
            drawHumanIcon(Offset(size.width * 0.2f, size.height * 0.2f), color, strokeWidth)
            drawHumanIcon(Offset(size.width * 0.8f, size.height * 0.2f), color, strokeWidth, isFemale = true)
        } else {
            // Draw a man and a robot
            drawHumanIcon(Offset(size.width * 0.2f, size.height * 0.2f), color, strokeWidth)
            drawRobotIcon(Offset(size.width * 0.8f, size.height * 0.2f), color, strokeWidth)
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHumanIcon(
    center: Offset,
    color: Color,
    strokeWidth: Float,
    isFemale: Boolean = false
) {
    val headRadius = 20.dp.toPx()
    val bodyHeight = 40.dp.toPx()
    val bodyWidth = 30.dp.toPx()

    // Head
    drawCircle(color = color, radius = headRadius, center = center.copy(y = center.y - headRadius - 5f), style = Stroke(width = strokeWidth))

    if (isFemale) {
        // Dress/Body (Triangle-ish)
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x - bodyWidth / 2, center.y + bodyHeight)
            lineTo(center.x + bodyWidth / 2, center.y + bodyHeight)
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
    } else {
        // Body (Simple line)
        drawLine(color = color, start = center, end = center.copy(y = center.y + bodyHeight), strokeWidth = strokeWidth)
    }

    // Arms
    drawLine(color = color, start = center.copy(y = center.y + 10f), end = Offset(center.x - 20.dp.toPx(), center.y + 30.dp.toPx()), strokeWidth = strokeWidth)
    drawLine(color = color, start = center.copy(y = center.y + 10f), end = Offset(center.x + 20.dp.toPx(), center.y + 30.dp.toPx()), strokeWidth = strokeWidth)

    // Legs (if not female or simple legs)
    if (!isFemale) {
        drawLine(color = color, start = center.copy(y = center.y + bodyHeight), end = Offset(center.x - 15.dp.toPx(), center.y + bodyHeight + 20.dp.toPx()), strokeWidth = strokeWidth)
        drawLine(color = color, start = center.copy(y = center.y + bodyHeight), end = Offset(center.x + 15.dp.toPx(), center.y + bodyHeight + 20.dp.toPx()), strokeWidth = strokeWidth)
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRobotIcon(
    center: Offset,
    color: Color,
    strokeWidth: Float
) {
    val size = 40.dp.toPx()
    // Head
    drawRect(color = color, topLeft = Offset(center.x - size / 3, center.y - size / 1.5f), size = androidx.compose.ui.geometry.Size(size / 1.5f, size / 2), style = Stroke(width = strokeWidth))
    // Body
    drawRect(color = color, topLeft = Offset(center.x - size / 2, center.y - size / 6), size = androidx.compose.ui.geometry.Size(size, size), style = Stroke(width = strokeWidth))
    // Antennae
    drawLine(color = color, start = Offset(center.x, center.y - size / 1.5f), end = Offset(center.x, center.y - size), strokeWidth = strokeWidth)
    // Eyes
    drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(center.x - 5.dp.toPx(), center.y - size / 2.5f))
    drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(center.x + 5.dp.toPx(), center.y - size / 2.5f))
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

fun getAiMove(board: List<Player?>, ai: Player, opponent: Player): Int {
    val winPatterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
        listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
    )

    // 1. Try to win
    for (pattern in winPatterns) {
        val cells = pattern.map { board[it] }
        if (cells.count { it == ai } == 2 && cells.count { it == null } == 1) {
            return pattern[cells.indexOf(null)]
        }
    }

    // 2. Try to block opponent
    for (pattern in winPatterns) {
        val cells = pattern.map { board[it] }
        if (cells.count { it == opponent } == 2 && cells.count { it == null } == 1) {
            return pattern[cells.indexOf(null)]
        }
    }

    // 3. Take center
    if (board[4] == null) return 4

    // 4. Take random
    val availableMoves = board.indices.filter { board[it] == null }
    return if (availableMoves.isNotEmpty()) availableMoves.random() else -1
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
