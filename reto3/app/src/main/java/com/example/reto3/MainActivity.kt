package com.example.reto3

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto3.ui.theme.Reto3Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Reto3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TicTacToeScreen()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Nuevo Juego")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}

@Composable
fun TicTacToeScreen() {
    val game = remember { TicTacToeGame() }
    var status by remember { mutableStateOf("") }
    var board by remember { mutableStateOf(CharArray(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.OPEN_SPOT }) }
    var gameOver by remember { mutableStateOf(false) }
    var isComputerTurn by remember { mutableStateOf(false) }

    // Estadísticas
    var humanWins by remember { mutableStateOf(0) }
    var computerWins by remember { mutableStateOf(0) }
    var ties by remember { mutableStateOf(0) }

    // Función para iniciar nuevo juego
    fun startNewGame() {
        game.clearBoard()
        board = CharArray(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.OPEN_SPOT }
        gameOver = false
        isComputerTurn = false

        status = if (game.isHumanFirst()) {
            "Empiezas tú"
        } else {
            "Android empieza"
        }

        // Si la computadora va primero
        if (!game.isHumanFirst()) {
            isComputerTurn = true
        }
    }

    // Manejar movimiento de la computadora
    LaunchedEffect(isComputerTurn) {
        if (isComputerTurn && !gameOver) {
            delay(500)
            val move = game.getComputerMove()
            if (game.setMove(TicTacToeGame.COMPUTER_PLAYER, move)) {
                board = board.copyOf()
                board[move] = TicTacToeGame.COMPUTER_PLAYER

                when (val winner = game.checkForWinner()) {
                    1 -> {
                        status = "¡Empate!"
                        ties++
                        gameOver = true
                        game.switchFirstPlayer()
                    }
                    2 -> {
                        status = "¡Ganaste!"
                        humanWins++
                        gameOver = true
                        game.switchFirstPlayer()
                    }
                    3 -> {
                        status = "Android ganó"
                        computerWins++
                        gameOver = true
                        game.switchFirstPlayer()
                    }
                    else -> status = "Tu turno"
                }
            }
            isComputerTurn = false
        }
    }

    // Inicializar el juego
    LaunchedEffect(Unit) {
        startNewGame()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Estadísticas
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Estadísticas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tú", fontWeight = FontWeight.Medium)
                        Text("$humanWins", fontSize = 20.sp, color = Color(0xFF4CAF50))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Empates", fontWeight = FontWeight.Medium)
                        Text("$ties", fontSize = 20.sp, color = Color(0xFF607D8B))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Android", fontWeight = FontWeight.Medium)
                        Text("$computerWins", fontSize = 20.sp, color = Color(0xFFF44336))
                    }
                }
            }
        }

        // Tablero de juego
        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    val index = row * 3 + col
                    Button(
                        onClick = {
                            if (!gameOver && !isComputerTurn && game.setMove(TicTacToeGame.HUMAN_PLAYER, index)) {
                                board[index] = TicTacToeGame.HUMAN_PLAYER

                                when (val winner = game.checkForWinner()) {
                                    1 -> {
                                        status = "¡Empate!"
                                        ties++
                                        gameOver = true
                                        game.switchFirstPlayer()
                                    }
                                    2 -> {
                                        status = "¡Ganaste!"
                                        humanWins++
                                        gameOver = true
                                        game.switchFirstPlayer()
                                    }
                                    3 -> {
                                        status = "Android ganó"
                                        computerWins++
                                        gameOver = true
                                        game.switchFirstPlayer()
                                    }
                                    0 -> {
                                        status = "Turno de Android"
                                        isComputerTurn = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp),
                        enabled = !gameOver && !isComputerTurn && board[index] == TicTacToeGame.OPEN_SPOT
                    ) {
                        Text(
                            text = if (board[index] == TicTacToeGame.OPEN_SPOT) "" else board[index].toString(),
                            fontSize = 28.sp,
                            color = when (board[index]) {
                                TicTacToeGame.HUMAN_PLAYER -> Color(0xFF4CAF50)
                                TicTacToeGame.COMPUTER_PLAYER -> Color(0xFFF44336)
                                else -> Color.Unspecified
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Estado del juego
        Text(
            text = status,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para nuevo juego
        Button(
            onClick = { startNewGame() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Nuevo Juego")
        }
    }
}
