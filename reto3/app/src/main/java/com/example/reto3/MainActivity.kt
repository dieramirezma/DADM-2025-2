package com.example.reto3

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
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
    
    // Game instance - keeping it at activity level for menu integration
    private lateinit var game: TicTacToeGame
    
    // Callback functions for UI updates from menu actions
    private var onNewGameCallback: (() -> Unit)? = null
    private var onDifficultyChangeCallback: ((TicTacToeGame.DifficultyLevel) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game = TicTacToeGame()
        
        setContent {
            Reto3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TicTacToeScreen(
                        gameInstance = game,
                        onNewGameRegistered = { callback -> onNewGameCallback = callback },
                        onDifficultyChangeRegistered = { callback -> onDifficultyChangeCallback = callback }
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_new_game -> {
                onNewGameCallback?.invoke()
                Toast.makeText(this, getString(R.string.toast_new_game_started), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_difficulty -> {
                showDifficultyDialog()
                true
            }
            R.id.menu_about -> {
                showAboutDialog()
                true
            }
            R.id.menu_quit -> {
                showQuitDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Shows difficulty selection dialog with radio buttons
     */
    private fun showDifficultyDialog() {
        val difficulties = arrayOf(
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_harder),
            getString(R.string.difficulty_expert)
        )
        
        val currentDifficulty = when (game.getDifficultyLevel()) {
            TicTacToeGame.DifficultyLevel.EASY -> 0
            TicTacToeGame.DifficultyLevel.HARDER -> 1
            TicTacToeGame.DifficultyLevel.EXPERT -> 2
        }
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_difficulty_title))
            .setSingleChoiceItems(difficulties, currentDifficulty) { dialog, which ->
                val newDifficulty = when (which) {
                    0 -> TicTacToeGame.DifficultyLevel.EASY
                    1 -> TicTacToeGame.DifficultyLevel.HARDER
                    2 -> TicTacToeGame.DifficultyLevel.EXPERT
                    else -> TicTacToeGame.DifficultyLevel.HARDER
                }
                
                game.setDifficultyLevel(newDifficulty)
                onDifficultyChangeCallback?.invoke(newDifficulty)
                
                val difficultyName = difficulties[which]
                Toast.makeText(
                    this,
                    getString(R.string.toast_difficulty_changed, difficultyName),
                    Toast.LENGTH_SHORT
                ).show()
                
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .show()
    }
    
    /**
     * Shows quit confirmation dialog
     */
    private fun showQuitDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_quit_title))
            .setMessage(getString(R.string.dialog_quit_message))
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.dialog_no), null)
            .show()
    }
    
    /**
     * Shows about dialog with developer information
     */
    private fun showAboutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.about_dialog, null)
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_about_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_ok), null)
            .show()
    }
}

@Composable
fun TicTacToeScreen(
    gameInstance: TicTacToeGame,
    onNewGameRegistered: (((() -> Unit)) -> Unit),
    onDifficultyChangeRegistered: ((((TicTacToeGame.DifficultyLevel) -> Unit)) -> Unit)
) {
    // Use the game instance from the activity
    val game = gameInstance
    var status by remember { mutableStateOf("") }
    var board by remember { mutableStateOf(CharArray(TicTacToeGame.BOARD_SIZE) { TicTacToeGame.OPEN_SPOT }) }
    var gameOver by remember { mutableStateOf(false) }
    var isComputerTurn by remember { mutableStateOf(false) }
    
    // Track difficulty changes for UI updates
    var currentDifficulty by remember { mutableStateOf(game.getDifficultyLevel()) }

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
    
    // Register callbacks with the activity for menu actions
    LaunchedEffect(Unit) {
        onNewGameRegistered { startNewGame() }
        onDifficultyChangeRegistered { newDifficulty -> 
            currentDifficulty = newDifficulty
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
        
        // Difficulty indicator
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val difficultyText = when (currentDifficulty) {
                    TicTacToeGame.DifficultyLevel.EASY -> "Fácil"
                    TicTacToeGame.DifficultyLevel.HARDER -> "Difícil"
                    TicTacToeGame.DifficultyLevel.EXPERT -> "Experto"
                }
                Text(
                    text = "Dificultad: $difficultyText",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = when (currentDifficulty) {
                        TicTacToeGame.DifficultyLevel.EASY -> Color(0xFF4CAF50)
                        TicTacToeGame.DifficultyLevel.HARDER -> Color(0xFFFF9800)
                        TicTacToeGame.DifficultyLevel.EXPERT -> Color(0xFFF44336)
                    }
                )
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

        // Botón para nuevo juego (también disponible en el menú)
        Button(
            onClick = { startNewGame() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Nuevo Juego")
        }
    }
}
