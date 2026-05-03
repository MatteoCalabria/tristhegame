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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.center
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
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
    Human, AI, Alien, Cat, Cowboy, Free
}

enum class PlayerSkin {
    Human, Robot, Alien, Cat, Cowboy
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
    var p1Skin by remember { mutableStateOf(PlayerSkin.Human) }
    var p2Skin by remember { mutableStateOf(PlayerSkin.Human) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            Screen.Home -> HomeScreen(
                onStartGame = { opponent, mode, s1, s2 ->
                    selectedOpponent = opponent
                    selectedMode = mode
                    p1Skin = s1
                    p2Skin = s2
                    currentScreen = Screen.Game
                },
                modifier = Modifier.padding(innerPadding),
            )
            Screen.Game -> GameScreen(
                opponentType = selectedOpponent,
                gameMode = selectedMode,
                p1Skin = p1Skin,
                p2Skin = p2Skin,
                onBackToHome = { currentScreen = Screen.Home },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun HomeScreen(onStartGame: (OpponentType, GameMode, PlayerSkin, PlayerSkin) -> Unit, modifier: Modifier = Modifier) {
    var step by remember { mutableStateOf(1) }
    var selectedOpponent by remember { mutableStateOf(OpponentType.Human) }
    var p1Skin by remember { mutableStateOf(PlayerSkin.Human) }
    var p2Skin by remember { mutableStateOf(PlayerSkin.Human) }

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

            when (step) {
                1 -> {
                    Text(
                        text = "Choose your opponents",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Button(
                        onClick = { selectedOpponent = OpponentType.Human; step = 4 },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) { Text(text = "Humans", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedOpponent = OpponentType.AI; step = 4 },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) { Text(text = "AI", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedOpponent = OpponentType.Alien; step = 4 },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) { Text(text = "Aliens", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedOpponent = OpponentType.Cat; step = 4 },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) { Text(text = "Cats", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedOpponent = OpponentType.Cowboy; step = 4 },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) { Text(text = "Cowboys", fontSize = 20.sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedOpponent = OpponentType.Free; step = 2 },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) { Text(text = "Free", fontSize = 20.sp) }
                }
                2 -> {
                    Text(
                        text = "Choose first player",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    val skins = listOf(PlayerSkin.Human, PlayerSkin.Alien, PlayerSkin.Cat, PlayerSkin.Cowboy)
                    skins.forEach { skin ->
                        Button(
                            onClick = { p1Skin = skin; step = 3 },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) { Text(text = skin.name, fontSize = 20.sp) }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(onClick = { step = 1 }, modifier = Modifier.fillMaxWidth(0.4f)) {
                        Text(text = "Back")
                    }
                }
                3 -> {
                    Text(
                        text = "Choose second player",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    val skins = listOf(PlayerSkin.Human, PlayerSkin.Alien, PlayerSkin.Cat, PlayerSkin.Cowboy)
                    skins.forEach { skin ->
                        Button(
                            onClick = { p2Skin = skin; step = 4 },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) { Text(text = skin.name, fontSize = 20.sp) }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth(0.4f)) {
                        Text(text = "Back")
                    }
                }
                4 -> {
                    Text(
                        text = "Choose the rules",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    val modes = listOf(GameMode.Single, GameMode.BestOf3, GameMode.BattleRoyale)
                    modes.forEach { mode ->
                        Button(
                            onClick = {
                                val finalP1Skin = if (selectedOpponent == OpponentType.Free) p1Skin else PlayerSkin.Human
                                val finalP2Skin = if (selectedOpponent == OpponentType.Free) p2Skin else when (selectedOpponent) {
                                    OpponentType.AI -> PlayerSkin.Robot
                                    OpponentType.Alien -> PlayerSkin.Alien
                                    OpponentType.Cat -> PlayerSkin.Cat
                                    OpponentType.Cowboy -> PlayerSkin.Cowboy
                                    else -> PlayerSkin.Human
                                }
                                onStartGame(selectedOpponent, mode, finalP1Skin, finalP2Skin)
                            },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text(text = when(mode) {
                                GameMode.Single -> "Single Match"
                                GameMode.BestOf3 -> "Best of 3"
                                GameMode.BattleRoyale -> "Battle Royale"
                            }, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(onClick = { step = if (selectedOpponent == OpponentType.Free) 3 else 1 }, modifier = Modifier.fillMaxWidth(0.4f)) {
                        Text(text = "Back")
                    }
                }
            }
        }
    }
}

@Composable
fun TrisBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val color = Color.Gray.copy(alpha = 0.1f)
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
fun GameScreen(
    opponentType: OpponentType,
    gameMode: GameMode,
    p1Skin: PlayerSkin,
    p2Skin: PlayerSkin,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scoreO by remember { mutableIntStateOf(0) }
    var scoreX by remember { mutableIntStateOf(0) }
    var startingPlayer by remember { mutableStateOf(if (Random.nextBoolean()) Player.O else Player.X) }

    var board by remember { mutableStateOf(List(9) { null as Player? }) }
    var currentPlayer by remember { mutableStateOf(startingPlayer) }
    var winningPattern by remember { mutableStateOf(null as List<Int>?) }
    var roundWinner by remember { mutableStateOf(null as Player?) }
    var matchWinner by remember { mutableStateOf(null as Player?) }
    var isDraw by remember { mutableStateOf(false) }

    val circleColor = Color(0xFF0000CC) // Deeper Blue
    val crossColor = when (opponentType) {
        OpponentType.Free -> Color(0xFFDD0000) // Stronger Red
        OpponentType.AI -> Color(0xFF00AA00) // Stronger Green
        OpponentType.Alien -> Color(0xFF444444) // Darker Gray for contrast
        OpponentType.Cat -> Color.Black
        OpponentType.Cowboy -> Color(0xFF8B4513) // Saddle Brown
        else -> Color(0xFFDD0000)
    }

    val nameO = when (opponentType) {
        OpponentType.Free -> "${p1Skin.name} 1"
        else -> "Circle"
    }
    val nameX = when (opponentType) {
        OpponentType.Free -> "${p2Skin.name} 2"
        OpponentType.Human -> "Cross"
        OpponentType.AI -> "AI"
        OpponentType.Alien -> "Aliens"
        OpponentType.Cat -> "Cats"
        OpponentType.Cowboy -> "Cowboys"
        else -> "Cross"
    }

    val onCellClick: (Int) -> Unit = { index ->
        if (board[index] == null && roundWinner == null && matchWinner == null && !isDraw) {
            // In VS AI modes, only allow clicking if it's Player O's turn
            val isP2AI = opponentType in listOf(OpponentType.AI, OpponentType.Alien, OpponentType.Cat, OpponentType.Cowboy)
            if (!isP2AI || currentPlayer == Player.O) {
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
                } else if (newBoard.none { it == null }) {
                    isDraw = true
                } else {
                    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
                }
            }
        }
    }

    // AI/Alien/Cat Logic
    LaunchedEffect(currentPlayer, roundWinner, matchWinner, isDraw) {
        val isP2AI = opponentType in listOf(OpponentType.AI, OpponentType.Alien, OpponentType.Cat, OpponentType.Cowboy)
        if (isP2AI && currentPlayer == Player.X && roundWinner == null && matchWinner == null && !isDraw) {
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
        val nextStart = if (startingPlayer == Player.O) Player.X else Player.O
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
        GameBackground(p1Skin, p2Skin, circleColor, crossColor, nameO, nameX)
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (gameMode != GameMode.Single) {
                    ScoreText(label = nameO, score = scoreO, color = circleColor)
                    ScoreText(label = nameX, score = scoreX, color = crossColor)
                }
            }

            val resultText = when {
                matchWinner != null -> "${if (matchWinner == Player.O) nameO else nameX} wins the match."
                roundWinner != null -> {
                    val winnerName = if (roundWinner == Player.O) nameO else nameX
                    if (gameMode == GameMode.BattleRoyale) {
                        val battleStatus = when {
                            scoreO > scoreX -> "$nameO is winning the battle"
                            scoreX > scoreO -> "$nameX is winning the battle"
                            else -> "The battle hangs in the balance"
                        }
                        "$winnerName wins the round. $battleStatus."
                    } else {
                        "$winnerName wins the round."
                    }
                }
                isDraw -> "It's a Draw."
                else -> "${if (currentPlayer == Player.O) nameO else nameX}'s Turn."
            }

            Text(
                text = resultText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Board(board, winningPattern, circleColor, crossColor, p1Skin, p2Skin, onCellClick)

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
fun GameBackground(p1Skin: PlayerSkin, p2Skin: PlayerSkin, circleColor: Color, crossColor: Color, nameO: String, nameX: String) {
    val textMeasurer = rememberTextMeasurer()
    val bgAlpha = 0.4f // Increased alpha for better contrast
    val textColorO = circleColor // Full opacity for text
    val textColorX = crossColor // Full opacity for text

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 4.dp.toPx()
        val verticalOffset = size.height * 0.08f

        // Draw Player 1 icon and name on the left
        val center1 = Offset(size.width * 0.15f, verticalOffset)
        drawSkinIcon(p1Skin, center1, circleColor.copy(alpha = bgAlpha), strokeWidth)
        
        val textLayoutResultO = textMeasurer.measure(
            text = nameO,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColorO
            )
        )
        drawText(
            textLayoutResult = textLayoutResultO,
            topLeft = Offset(center1.x - textLayoutResultO.size.width / 2f, center1.y + 50.dp.toPx())
        )

        // Draw Player 2 icon and name on the right
        val center2 = Offset(size.width * 0.85f, verticalOffset)
        drawSkinIcon(p2Skin, center2, crossColor.copy(alpha = bgAlpha), strokeWidth)

        val textLayoutResultX = textMeasurer.measure(
            text = nameX,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColorX
            )
        )
        drawText(
            textLayoutResult = textLayoutResultX,
            topLeft = Offset(center2.x - textLayoutResultX.size.width / 2f, center2.y + 50.dp.toPx())
        )
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSkinIcon(skin: PlayerSkin, center: Offset, color: Color, strokeWidth: Float) {
    when (skin) {
        PlayerSkin.Human -> drawHumanIcon(center, color, strokeWidth)
        PlayerSkin.Robot -> drawRobotIcon(center, color, strokeWidth)
        PlayerSkin.Alien -> drawStarshipIcon(center, color, strokeWidth)
        PlayerSkin.Cat -> drawCatIcon(center, color, strokeWidth)
        PlayerSkin.Cowboy -> drawCowboyIcon(center, color, strokeWidth)
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHumanIcon(
    center: Offset,
    color: Color,
    strokeWidth: Float,
    isFemale: Boolean = false,
    isCombined: Boolean = false
) {
    val headRadius = 15.dp.toPx()
    val bodyHeight = 30.dp.toPx()
    val bodyWidth = 22.dp.toPx()

    // Head
    drawCircle(color = color, radius = headRadius, center = center.copy(y = center.y - headRadius - 5f), style = Stroke(width = strokeWidth))

    if (isFemale || isCombined) {
        // Dress/Body (Triangle-ish)
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x, center.y)
            if (isCombined) {
                // Combined: half straight, half skirt
                lineTo(center.x, center.y + bodyHeight)
                lineTo(center.x + bodyWidth / 2, center.y + bodyHeight)
            } else {
                lineTo(center.x - bodyWidth / 2, center.y + bodyHeight)
                lineTo(center.x + bodyWidth / 2, center.y + bodyHeight)
            }
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
        
        if (isCombined) {
            // Draw the other straight half
            drawLine(color = color, start = center, end = center.copy(y = center.y + bodyHeight), strokeWidth = strokeWidth)
        }
    } else {
        // Body (Simple line)
        drawLine(color = color, start = center, end = center.copy(y = center.y + bodyHeight), strokeWidth = strokeWidth)
    }

    // Arms
    drawLine(color = color, start = center.copy(y = center.y + 10f), end = Offset(center.x - 20.dp.toPx(), center.y + 30.dp.toPx()), strokeWidth = strokeWidth)
    drawLine(color = color, start = center.copy(y = center.y + 10f), end = Offset(center.x + 20.dp.toPx(), center.y + 30.dp.toPx()), strokeWidth = strokeWidth)

    // Legs
    if (isFemale || isCombined) {
        // Legs
        if (isCombined) {
            // One straight leg, one skirt leg
            drawLine(color = color, start = center.copy(y = center.y + bodyHeight), end = Offset(center.x - 15.dp.toPx(), center.y + bodyHeight + 20.dp.toPx()), strokeWidth = strokeWidth)
            drawLine(color = color, start = Offset(center.x + 10.dp.toPx(), center.y + bodyHeight), end = Offset(center.x + 10.dp.toPx(), center.y + bodyHeight + 20.dp.toPx()), strokeWidth = strokeWidth)
        } else {
            drawLine(color = color, start = Offset(center.x - 10.dp.toPx(), center.y + bodyHeight), end = Offset(center.x - 10.dp.toPx(), center.y + bodyHeight + 20.dp.toPx()), strokeWidth = strokeWidth)
            drawLine(color = color, start = Offset(center.x + 10.dp.toPx(), center.y + bodyHeight), end = Offset(center.x + 10.dp.toPx(), center.y + bodyHeight + 20.dp.toPx()), strokeWidth = strokeWidth)
        }
    } else {
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
    // Legs
    drawLine(color = color, start = Offset(center.x - size / 4, center.y + size - size / 6), end = Offset(center.x - size / 4, center.y + size + size / 3), strokeWidth = strokeWidth)
    drawLine(color = color, start = Offset(center.x + size / 4, center.y + size - size / 6), end = Offset(center.x + size / 4, center.y + size + size / 3), strokeWidth = strokeWidth)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStarshipIcon(
    center: Offset,
    color: Color,
    strokeWidth: Float
) {
    val width = 45.dp.toPx()
    val height = 25.dp.toPx()
    
    // Main body (Saucer)
    drawOval(
        color = color,
        topLeft = Offset(center.x - width / 2, center.y - height / 4),
        size = androidx.compose.ui.geometry.Size(width, height),
        style = Stroke(width = strokeWidth)
    )
    
    // Dome
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - width / 4, center.y - height / 2),
        size = androidx.compose.ui.geometry.Size(width / 2, height),
        style = Stroke(width = strokeWidth)
    )
    
    // Antennas
    drawLine(
        color = color,
        start = Offset(center.x, center.y - height / 2),
        end = Offset(center.x, center.y - height * 0.8f),
        strokeWidth = strokeWidth
    )
    
    // Lights on the saucer
    for (i in -2..2) {
        drawCircle(
            color = color,
            radius = 2.dp.toPx(),
            center = Offset(center.x + i * width / 6, center.y + height / 8)
        )
    }

    // Beams
    drawLine(
        color = color,
        start = Offset(center.x - width / 4, center.y + height / 4),
        end = Offset(center.x - width / 3, center.y + height * 1.5f),
        strokeWidth = strokeWidth / 2,
        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    drawLine(
        color = color,
        start = Offset(center.x + width / 4, center.y + height / 4),
        end = Offset(center.x + width / 3, center.y + height * 1.5f),
        strokeWidth = strokeWidth / 2,
        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAlienFace(center: Offset, color: Color) {
    val width = 30.dp.toPx()
    val height = 35.dp.toPx()
    val strokeWidth = 2.dp.toPx()

    // Head
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x, center.y - height / 2)
        quadraticTo(center.x + width / 2, center.y - height / 2, center.x + width / 2, center.y)
        quadraticTo(center.x + width / 2, center.y + height / 2, center.x, center.y + height / 2)
        quadraticTo(center.x - width / 2, center.y + height / 2, center.x - width / 2, center.y)
        quadraticTo(center.x - width / 2, center.y - height / 2, center.x, center.y - height / 2)
        close()
    }
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth))

    // Eyes
    val leftEye = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x - 10.dp.toPx(), center.y - 2.dp.toPx())
        quadraticTo(center.x - 5.dp.toPx(), center.y - 12.dp.toPx(), center.x - 2.dp.toPx(), center.y - 2.dp.toPx())
        quadraticTo(center.x - 5.dp.toPx(), center.y + 2.dp.toPx(), center.x - 10.dp.toPx(), center.y - 2.dp.toPx())
        close()
    }
    drawPath(path = leftEye, color = color)

    val rightEye = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x + 10.dp.toPx(), center.y - 2.dp.toPx())
        quadraticTo(center.x + 5.dp.toPx(), center.y - 12.dp.toPx(), center.x + 2.dp.toPx(), center.y - 2.dp.toPx())
        quadraticTo(center.x + 5.dp.toPx(), center.y + 2.dp.toPx(), center.x + 10.dp.toPx(), center.y - 2.dp.toPx())
        close()
    }
    drawPath(path = rightEye, color = color)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCatIcon(
    center: Offset,
    color: Color,
    strokeWidth: Float
) {
    val headRadius = 18.dp.toPx()
    val bodyWidth = 35.dp.toPx()
    val bodyHeight = 45.dp.toPx()
    
    // Body (Sitting)
    val bodyPath = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x - bodyWidth / 3, center.y + headRadius)
        quadraticTo(center.x - bodyWidth / 2, center.y + bodyHeight, center.x - bodyWidth / 2, center.y + bodyHeight)
        lineTo(center.x + bodyWidth / 2, center.y + bodyHeight)
        quadraticTo(center.x + bodyWidth / 2, center.y + headRadius, center.x + bodyWidth / 3, center.y + headRadius)
        close()
    }
    drawPath(path = bodyPath, color = color, style = Stroke(width = strokeWidth))

    // Tail
    val tailPath = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x + bodyWidth / 2, center.y + bodyHeight - 5.dp.toPx())
        quadraticTo(center.x + bodyWidth, center.y + bodyHeight / 2, center.x + bodyWidth / 1.5f, center.y + bodyHeight / 4)
    }
    drawPath(path = tailPath, color = color, style = Stroke(width = strokeWidth))

    // Face
    drawCircle(color = color, radius = headRadius, center = center, style = Stroke(width = strokeWidth))
    
    // Ears
    val leftEar = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x - headRadius * 0.8f, center.y - headRadius * 0.5f)
        lineTo(center.x - headRadius * 1.2f, center.y - headRadius * 1.5f)
        lineTo(center.x - headRadius * 0.2f, center.y - headRadius * 0.9f)
    }
    drawPath(path = leftEar, color = color, style = Stroke(width = strokeWidth))
    
    val rightEar = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x + headRadius * 0.8f, center.y - headRadius * 0.5f)
        lineTo(center.x + headRadius * 1.2f, center.y - headRadius * 1.5f)
        lineTo(center.x + headRadius * 0.2f, center.y - headRadius * 0.9f)
    }
    drawPath(path = rightEar, color = color, style = Stroke(width = strokeWidth))
    
    // Eyes
    drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(center.x - 7.dp.toPx(), center.y - 2.dp.toPx()))
    drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(center.x + 7.dp.toPx(), center.y - 2.dp.toPx()))
    
    // Nose
    drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(center.x, center.y + 4.dp.toPx()))
    
    // Whiskers
    drawLine(color = color, start = Offset(center.x - 8.dp.toPx(), center.y + 7.dp.toPx()), end = Offset(center.x - 25.dp.toPx(), center.y + 4.dp.toPx()), strokeWidth = strokeWidth / 2)
    drawLine(color = color, start = Offset(center.x - 8.dp.toPx(), center.y + 9.dp.toPx()), end = Offset(center.x - 25.dp.toPx(), center.y + 14.dp.toPx()), strokeWidth = strokeWidth / 2)
    
    drawLine(color = color, start = Offset(center.x + 8.dp.toPx(), center.y + 7.dp.toPx()), end = Offset(center.x + 25.dp.toPx(), center.y + 4.dp.toPx()), strokeWidth = strokeWidth / 2)
    drawLine(color = color, start = Offset(center.x + 8.dp.toPx(), center.y + 9.dp.toPx()), end = Offset(center.x + 25.dp.toPx(), center.y + 14.dp.toPx()), strokeWidth = strokeWidth / 2)
}

@Composable
fun ScoreText(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(text = score.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun Board(board: List<Player?>, winningPattern: List<Int>?, circleColor: Color, crossColor: Color, p1Skin: PlayerSkin, p2Skin: PlayerSkin, onCellClick: (Int) -> Unit) {
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
                            circleColor = circleColor,
                            crossColor = crossColor,
                            p1Skin = p1Skin,
                            p2Skin = p2Skin,
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
                Player.X -> crossColor
                Player.O -> circleColor
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
fun Cell(player: Player?, circleColor: Color, crossColor: Color, p1Skin: PlayerSkin, p2Skin: PlayerSkin, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, Color.Gray)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            val color = if (player == Player.O) circleColor else crossColor
            val skin = if (player == Player.O) p1Skin else p2Skin

            when (skin) {
                PlayerSkin.Cat -> Canvas(modifier = Modifier.size(48.dp)) {
                    drawCatFootprint(this.size.center, color)
                }
                PlayerSkin.Alien -> Canvas(modifier = Modifier.size(48.dp)) {
                    drawAlienFace(this.size.center, color)
                }
                PlayerSkin.Cowboy -> Canvas(modifier = Modifier.size(48.dp)) {
                    drawHorseshoe(this.size.center, color, 4.dp.toPx())
                }
                else -> Text(
                    text = if (player == Player.O) "O" else "X",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCowboyIcon(center: Offset, color: Color, strokeWidth: Float) {
    val hatWidth = 34.dp.toPx()
    val hatHeight = 18.dp.toPx()
    val brimWidth = 54.dp.toPx()
    val brimHeight = 10.dp.toPx()
    
    // Curved Brim
    val brimPath = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x - brimWidth / 2, center.y + 2.dp.toPx())
        quadraticTo(center.x, center.y + brimHeight, center.x + brimWidth / 2, center.y + 2.dp.toPx())
        quadraticTo(center.x, center.y + 6.dp.toPx(), center.x - brimWidth / 2, center.y + 2.dp.toPx())
    }
    drawPath(path = brimPath, color = color, style = Stroke(width = strokeWidth))
    
    // Hat top with a crease
    val crownPath = androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x - hatWidth / 2, center.y + 2.dp.toPx())
        lineTo(center.x - hatWidth / 2, center.y - hatHeight * 0.7f)
        quadraticTo(center.x - hatWidth / 2, center.y - hatHeight, center.x - hatWidth / 4, center.y - hatHeight)
        // Crease in the middle
        lineTo(center.x, center.y - hatHeight * 0.85f)
        lineTo(center.x + hatWidth / 4, center.y - hatHeight)
        quadraticTo(center.x + hatWidth / 2, center.y - hatHeight, center.x + hatWidth / 2, center.y - hatHeight * 0.7f)
        lineTo(center.x + hatWidth / 2, center.y + 2.dp.toPx())
    }
    drawPath(path = crownPath, color = color, style = Stroke(width = strokeWidth))
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHorseshoe(center: Offset, color: Color, strokeWidth: Float) {
    val radius = 18.dp.toPx()
    val path = androidx.compose.ui.graphics.Path().apply {
        // More defined U shape
        moveTo(center.x - radius * 0.7f, center.y - radius)
        lineTo(center.x - radius * 0.7f, center.y - radius * 0.3f)
        quadraticTo(center.x - radius * 0.7f, center.y + radius, center.x, center.y + radius)
        quadraticTo(center.x + radius * 0.7f, center.y + radius, center.x + radius * 0.7f, center.y - radius * 0.3f)
        lineTo(center.x + radius * 0.7f, center.y - radius)
    }
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Butt))

    // Add "nail holes" for detail
    val holeRadius = 1.5.dp.toPx()
    val holeOffsets = listOf(
        Offset(center.x - radius * 0.7f, center.y + radius * 0.2f),
        Offset(center.x - radius * 0.45f, center.y + radius * 0.75f),
        Offset(center.x + radius * 0.45f, center.y + radius * 0.75f),
        Offset(center.x + radius * 0.7f, center.y + radius * 0.2f)
    )
    holeOffsets.forEach { offset ->
        drawCircle(color = color, radius = holeRadius, center = offset)
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCatFootprint(center: Offset, color: Color) {
    val mainPadRadius = 12.dp.toPx()
    val toeRadius = 5.dp.toPx()
    
    // Main pad
    drawCircle(color = color, radius = mainPadRadius, center = center.copy(y = center.y + 5.dp.toPx()))
    
    // Toes
    drawCircle(color = color, radius = toeRadius, center = Offset(center.x - 12.dp.toPx(), center.y - 8.dp.toPx()))
    drawCircle(color = color, radius = toeRadius, center = Offset(center.x - 4.dp.toPx(), center.y - 14.dp.toPx()))
    drawCircle(color = color, radius = toeRadius, center = Offset(center.x + 4.dp.toPx(), center.y - 14.dp.toPx()))
    drawCircle(color = color, radius = toeRadius, center = Offset(center.x + 12.dp.toPx(), center.y - 8.dp.toPx()))
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
