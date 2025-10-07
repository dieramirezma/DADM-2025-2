package com.example.reto3

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.reto3.models.GameStatus
import com.example.reto3.repository.GameRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var game: TicTacToeGame
    private lateinit var boardView: BoardView
    private lateinit var statusText: TextView
    private lateinit var repository: GameRepository
    private lateinit var auth: FirebaseAuth

    private var gameId: String? = null
    private var mySymbol: String = "X" // X o O
    private var gameOver = false

    private var moveSoundPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        repository = GameRepository()

        // Obtener datos del intent
        gameId = intent.getStringExtra("GAME_ID")
        mySymbol = intent.getStringExtra("PLAYER_SYMBOL") ?: "X"

        if (gameId == null) {
            Toast.makeText(this, "Invalid game", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        game = TicTacToeGame()
        initializeViews()
        loadSound()
        observeGameChanges()
    }

    private fun initializeViews() {
        boardView = findViewById(R.id.board_view)
        statusText = findViewById(R.id.status_text)

        boardView.setGame(game)

        // Configurar listener para toques en el tablero
        boardView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !gameOver) {
                onBoardTouch(event.x, event.y)
            }
            true
        }

        updateStatus("Waiting for game to start...")
    }

    private fun loadSound() {
        try {
            moveSoundPlayer = MediaPlayer.create(this, R.raw.human_sound)
        } catch (e: Exception) {
            // Sonido no disponible
        }
    }

    private fun observeGameChanges() {
        lifecycleScope.launch {
            gameId?.let { id ->
                repository.observeGame(id).collectLatest { gameData ->
                    gameData?.let { updateGameState(it) }
                }
            }
        }
    }

    private fun updateGameState(gameData: com.example.reto3.models.Game) {
        // Actualizar tablero local
        game.setBoardStateFromList(gameData.board)
        boardView.invalidate()

        // Actualizar estado del juego
        when (gameData.status) {
            GameStatus.WAITING -> {
                updateStatus("Waiting for opponent...")
                gameOver = true
            }
            GameStatus.PLAYING -> {
                gameOver = false
                if (gameData.currentTurn == mySymbol) {
                    updateStatus("Your turn ($mySymbol)")
                } else {
                    updateStatus("Opponent's turn")
                }
            }
            GameStatus.FINISHED -> {
                gameOver = true
                handleGameEnd(gameData.winner)
            }
        }

        // Verificar ganador localmente también
        checkWinner()
    }

    private fun onBoardTouch(x: Float, y: Float) {
        if (gameOver) {
            Toast.makeText(this, "Game is over", Toast.LENGTH_SHORT).show()
            return
        }

        val cell = boardView.getBoardCellFromCoordinates(x, y)
        if (cell == -1) return

        // Verificar si es el turno del jugador
        lifecycleScope.launch {
            gameId?.let { id ->
                val result = repository.makeMove(id, cell, mySymbol)
                result.onSuccess {
                    playSound()
                }.onFailure { error ->
                    Toast.makeText(
                        this@MainActivity,
                        error.message ?: "Invalid move",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkWinner() {
        val winner = game.checkForWinner()
        winner?.let {
            gameOver = true

            lifecycleScope.launch {
                gameId?.let { id ->
                    val winnerStr = when (winner) {
                        'T' -> "TIE"
                        else -> winner.toString()
                    }
                    repository.updateGameWinner(id, winnerStr)
                }
            }
        }
    }

    private fun handleGameEnd(winner: String?) {
        val message = when (winner) {
            mySymbol -> "You win! 🎉"
            "TIE" -> "It's a tie! 🤝"
            null -> "Game ended"
            else -> "You lose 😢"
        }

        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("New Game") { _, _ ->
                finish()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun updateStatus(message: String) {
        statusText.text = message
    }

    private fun playSound() {
        try {
            moveSoundPlayer?.start()
        } catch (e: Exception) {
            // Sonido no disponible
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_leave -> {
                leaveGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun leaveGame() {
        AlertDialog.Builder(this)
            .setTitle("Leave Game")
            .setMessage("Are you sure you want to leave?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    gameId?.let { id ->
                        auth.currentUser?.uid?.let { uid ->
                            repository.leaveGame(id, uid)
                        }
                    }
                    finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        moveSoundPlayer?.release()
        moveSoundPlayer = null
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        leaveGame()
    }
}
