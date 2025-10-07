package com.example.reto3

import android.app.AlertDialog
import android.content.SharedPreferences
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
 * Actividad principal del juego Tic Tac Toe OFFLINE
 * Implementa un juego de tres en raya con diferentes niveles de dificultad
 * y menús de opciones para mejorar la experiencia del usuario.
 *
 * Características principales:
 * - Soporte para orientación portrait y landscape
 * - Preservación del estado durante rotación de pantalla
 * - Almacenamiento persistente de estadísticas y configuraciones
 */
class MainActivityOffline : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "TicTacToePrefs"
        private const val KEY_HUMAN_WINS = "humanWins"
        private const val KEY_COMPUTER_WINS = "computerWins"
        private const val KEY_TIES = "ties"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_HUMAN_FIRST = "humanFirst"

        // Keys para guardar el estado del juego durante rotación
        private const val STATE_BOARD = "board"
        private const val STATE_GAME_OVER = "gameOver"
        private const val STATE_IS_COMPUTER_TURN = "isComputerTurn"
        private const val STATE_HUMAN_WINS = "stateHumanWins"
        private const val STATE_COMPUTER_WINS = "stateComputerWins"
        private const val STATE_TIES = "stateTies"
        private const val STATE_STATUS_TEXT = "statusText"
    }

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

    // SharedPreferences para persistencia
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Inicializar juego y componentes UI
        game = TicTacToeGame()
        initializeViews()

        // Restaurar estado si es necesario
        if (savedInstanceState != null) {
            restoreGameState(savedInstanceState)
        } else {
            // Cargar configuraciones persistentes
            loadPersistentData()
            startNewGame()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameScope.cancel()
        releaseSounds()
        savePersistentData()
    }

    override fun onPause() {
        super.onPause()
        releaseSounds()
        savePersistentData()
    }

    override fun onResume() {
        super.onResume()
        loadSounds()
    }

    /**
     * Guarda el estado del juego cuando la configuración cambia (rotación de pantalla)
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Guardar estado del tablero
        val board = CharArray(TicTacToeGame.BOARD_SIZE)
        for (i in 0 until TicTacToeGame.BOARD_SIZE) {
            board[i] = game.getBoardOccupant(i)
        }
        outState.putCharArray(STATE_BOARD, board)

        // Guardar estado del juego
        outState.putBoolean(STATE_GAME_OVER, gameOver)
        outState.putBoolean(STATE_IS_COMPUTER_TURN, isComputerTurn)

        // Guardar estadísticas actuales
        outState.putInt(STATE_HUMAN_WINS, humanWins)
        outState.putInt(STATE_COMPUTER_WINS, computerWins)
        outState.putInt(STATE_TIES, ties)

        // Guardar texto de estado
        outState.putString(STATE_STATUS_TEXT, statusText.text.toString())
    }

    /**
     * Restaura el estado del juego después de un cambio de configuración
     */
    private fun restoreGameState(savedInstanceState: Bundle) {
        // Restaurar estadísticas persistentes primero
        loadPersistentData()

        // Restaurar estado del tablero
        val boardState = savedInstanceState.getCharArray(STATE_BOARD)
        if (boardState != null) {
            for (i in boardState.indices) {
                if (boardState[i] != TicTacToeGame.OPEN_SPOT) {
                    game.setMove(boardState[i], i)
                }
            }
        }

        // Restaurar estado del juego
        gameOver = savedInstanceState.getBoolean(STATE_GAME_OVER, false)
        isComputerTurn = savedInstanceState.getBoolean(STATE_IS_COMPUTER_TURN, false)

        // Restaurar estadísticas de la sesión actual
        humanWins = savedInstanceState.getInt(STATE_HUMAN_WINS, humanWins)
        computerWins = savedInstanceState.getInt(STATE_COMPUTER_WINS, computerWins)
        ties = savedInstanceState.getInt(STATE_TIES, ties)

        // Restaurar texto de estado
        val statusTextString = savedInstanceState.getString(STATE_STATUS_TEXT)
        if (statusTextString != null) {
            statusText.text = statusTextString
        }

        // Actualizar UI
        updateStatistics()
        boardView.invalidate()

        // Si es turno de la computadora y el juego no ha terminado, hacer el movimiento
        if (isComputerTurn && !gameOver) {
            handler.postDelayed({
                makeComputerMove()
            }, 1000)
        }
    }

    /**
     * Carga datos persistentes desde SharedPreferences
     */
    private fun loadPersistentData() {
        humanWins = sharedPreferences.getInt(KEY_HUMAN_WINS, 0)
        computerWins = sharedPreferences.getInt(KEY_COMPUTER_WINS, 0)
        ties = sharedPreferences.getInt(KEY_TIES, 0)

        // Cargar configuración de dificultad
        val difficultyOrdinal = sharedPreferences.getInt(KEY_DIFFICULTY, TicTacToeGame.DifficultyLevel.EXPERT.ordinal)
        val difficulty = TicTacToeGame.DifficultyLevel.values()[difficultyOrdinal]
        game.setDifficultyLevel(difficulty)

        // Cargar quién inicia primero
        val humanFirst = sharedPreferences.getBoolean(KEY_HUMAN_FIRST, true)
        if (game.isHumanFirst() != humanFirst) {
            game.switchFirstPlayer()
        }
    }

    /**
     * Guarda datos persistentes en SharedPreferences
     */
    private fun savePersistentData() {
        with(sharedPreferences.edit()) {
            putInt(KEY_HUMAN_WINS, humanWins)
            putInt(KEY_COMPUTER_WINS, computerWins)
            putInt(KEY_TIES, ties)
            putInt(KEY_DIFFICULTY, game.getDifficultyLevel().ordinal)
            putBoolean(KEY_HUMAN_FIRST, game.isHumanFirst())
            apply()
        }
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

        // Guardar estadísticas actualizadas
        savePersistentData()
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
            R.id.menu_reset_stats -> {
                showResetStatsDialog()
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

            // Guardar la configuración de dificultad
            savePersistentData()

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

    /**
     * Muestra el diálogo de confirmación para resetear estadísticas
     */
    private fun showResetStatsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Statistics")
        builder.setMessage("Are you sure you want to reset all statistics?")

        builder.setPositiveButton(R.string.dialog_yes) { _, _ ->
            resetStatistics()
            Toast.makeText(this, "Statistics reset successfully", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(R.string.dialog_no) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    /**
     * Resetea todas las estadísticas
     */
    private fun resetStatistics() {
        humanWins = 0
        computerWins = 0
        ties = 0
        updateStatistics()
        savePersistentData()
    }
}
