package com.example.reto3

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

/**
 * Actividad principal del juego Tic Tac Toe
 * Implementa un juego de tres en raya con diferentes niveles de dificultad
 * y menús de opciones para mejorar la experiencia del usuario
 */
class MainActivity : AppCompatActivity() {

    // Juego y UI components
    private lateinit var game: TicTacToeGame
    private lateinit var boardView: BoardView
    private lateinit var statusText: TextView
    private lateinit var humanWinsText: TextView
    private lateinit var tiesText: TextView
    private lateinit var computerWinsText: TextView

    // Game state
    private var gameOver = false
    private var isComputerTurn = false

    // Estadísticas del juego
    private var humanWins = 0
    private var computerWins = 0
    private var ties = 0

    // MediaPlayer para sonidos
    private var humanSoundPlayer: MediaPlayer? = null
    private var computerSoundPlayer: MediaPlayer? = null

    // Handler para delays
    private val handler = Handler(Looper.getMainLooper())

    // Coroutine para manejo asíncrono
    private var gameScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar juego y componentes UI
        game = TicTacToeGame()
        initializeViews()
        startNewGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameScope.cancel()
        releaseSounds()
    }

    override fun onPause() {
        super.onPause()
        releaseSounds()
    }

    override fun onResume() {
        super.onResume()
        loadSounds()
    }

    /**
     * Inicializa las vistas y vincula los botones
     */
    private fun initializeViews() {
        // Inicializar BoardView
        boardView = findViewById(R.id.boardView)
        boardView.setGame(game)

        // Configurar listener para toques en el tablero
        boardView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onBoardTouch(event.x, event.y)
            }
            true
        }

        // Inicializar textos de estado y estadísticas
        statusText = findViewById(R.id.tvStatus)
        humanWinsText = findViewById(R.id.tvHumanWins)
        tiesText = findViewById(R.id.tvTies)
        computerWinsText = findViewById(R.id.tvComputerWins)

        // Actualizar estadísticas visuales
        updateStatistics()
    }

    /**
     * Inicia un nuevo juego
     */
    private fun startNewGame() {
        game.clearBoard()
        gameOver = false
        isComputerTurn = false

        // Invalidar la vista para redibujar el tablero limpio
        boardView.invalidate()

        // Determinar quién empieza y mostrar mensaje apropiado
        if (game.isHumanFirst()) {
            statusText.text = getString(R.string.first_human)
        } else {
            statusText.text = getString(R.string.android_starts)
            isComputerTurn = true
            // Hacer movimiento de computadora después de un delay de 1 segundo
            handler.postDelayed({
                makeComputerMove()
            }, 1000)
        }
    }

    /**
     * Maneja los toques en el tablero
     */
    private fun onBoardTouch(x: Float, y: Float) {
        if (gameOver || isComputerTurn) {
            return
        }

        val position = boardView.getBoardCellFromCoordinates(x, y)
        if (position != -1 && game.setMove(TicTacToeGame.HUMAN_PLAYER, position)) {
            // Reproducir sonido del jugador humano
            humanSoundPlayer?.start()

            // Actualizar la vista
            boardView.invalidate()

            // Verificar ganador
            val winner = game.checkForWinner()
            if (winner != 0) {
                handleGameEnd(winner)
            } else {
                // Turno de la computadora
                statusText.text = getString(R.string.turn_computer)
                isComputerTurn = true
                handler.postDelayed({
                    makeComputerMove()
                }, 1000) // Delay de 1 segundo antes del movimiento de la computadora
            }
        }
    }

    /**
     * Hace el movimiento de la computadora
     */
    private fun makeComputerMove() {
        if (gameOver) return

        val move = game.getComputerMove()
        if (move != -1 && game.setMove(TicTacToeGame.COMPUTER_PLAYER, move)) {
            // Reproducir sonido de la computadora
            computerSoundPlayer?.start()

            // Actualizar la vista
            boardView.invalidate()

            // Verificar ganador
            val winner = game.checkForWinner()
            if (winner != 0) {
                handleGameEnd(winner)
            } else {
                statusText.text = getString(R.string.turn_human)
            }
        }
        isComputerTurn = false
    }

    /**
     * Maneja el final del juego
     */
    private fun handleGameEnd(winner: Int) {
        gameOver = true
        isComputerTurn = false

        // Mostrar resultado y actualizar estadísticas
        when (winner) {
            1 -> {
                statusText.text = getString(R.string.result_tie)
                ties++
            }
            2 -> {
                statusText.text = getString(R.string.result_human_wins)
                humanWins++
            }
            3 -> {
                statusText.text = getString(R.string.result_computer_wins)
                computerWins++
            }
        }

        // Cambiar quién empieza la próxima vez
        game.switchFirstPlayer()
        updateStatistics()
    }

    /**
     * Actualiza las estadísticas en pantalla
     */
    private fun updateStatistics() {
        humanWinsText.text = humanWins.toString()
        tiesText.text = ties.toString()
        computerWinsText.text = computerWins.toString()
    }

    /**
     * Carga los sonidos para el juego
     */
    private fun loadSounds() {
        try {
            humanSoundPlayer = MediaPlayer.create(this, R.raw.human_sound)
            computerSoundPlayer = MediaPlayer.create(this, R.raw.ai_sound)
        } catch (e: Exception) {
            // Si no se pueden cargar los sonidos, continúa sin ellos
            e.printStackTrace()
        }
    }

    /**
     * Libera los recursos de sonido
     */
    private fun releaseSounds() {
        humanSoundPlayer?.release()
        computerSoundPlayer?.release()
        humanSoundPlayer = null
        computerSoundPlayer = null
    }

    // Menú de opciones
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_new_game -> {
                startNewGame()
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
     * Muestra el diálogo de selección de dificultad
     */
    private fun showDifficultyDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.difficulty_dialog_title)

        // Opciones de dificultad
        val difficulties = arrayOf(
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_harder),
            getString(R.string.difficulty_expert)
        )

        // Obtener la dificultad actual seleccionada
        val currentDifficulty = when (game.getDifficultyLevel()) {
            TicTacToeGame.DifficultyLevel.EASY -> 0
            TicTacToeGame.DifficultyLevel.HARDER -> 1
            TicTacToeGame.DifficultyLevel.EXPERT -> 2
        }

        builder.setSingleChoiceItems(difficulties, currentDifficulty) { dialog, which ->
            val selectedLevel = when (which) {
                0 -> TicTacToeGame.DifficultyLevel.EASY
                1 -> TicTacToeGame.DifficultyLevel.HARDER
                2 -> TicTacToeGame.DifficultyLevel.EXPERT
                else -> TicTacToeGame.DifficultyLevel.EXPERT
            }

            // Actualizar dificultad en el juego
            game.setDifficultyLevel(selectedLevel)

            // Mostrar mensaje Toast
            val toastMessage = when (selectedLevel) {
                TicTacToeGame.DifficultyLevel.EASY -> getString(R.string.difficulty_easy_selected)
                TicTacToeGame.DifficultyLevel.HARDER -> getString(R.string.difficulty_harder_selected)
                TicTacToeGame.DifficultyLevel.EXPERT -> getString(R.string.difficulty_expert_selected)
            }
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    /**
     * Muestra el diálogo About con información del desarrollador
     */
    private fun showAboutDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.about_dialog, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    /**
     * Muestra el diálogo de confirmación para salir del juego
     */
    private fun showQuitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.quit_dialog_title)
        builder.setMessage(R.string.quit_dialog_message)

        builder.setPositiveButton(R.string.dialog_yes) { _, _ ->
            finish() // Cierra la aplicación
        }

        builder.setNegativeButton(R.string.dialog_no) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}
