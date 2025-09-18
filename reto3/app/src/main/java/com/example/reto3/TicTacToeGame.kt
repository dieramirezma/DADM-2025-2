package com.example.reto3

import kotlin.random.Random

class TicTacToeGame {

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '
        const val BOARD_SIZE = 9
    }

    // Enum para niveles de dificultad
    enum class DifficultyLevel {
        EASY, HARDER, EXPERT
    }

    private val board = CharArray(BOARD_SIZE) { OPEN_SPOT }
    private var humanFirst = true
    private var difficultyLevel = DifficultyLevel.HARDER // Dificultad por defecto

    fun clearBoard() {
        for (i in board.indices) board[i] = OPEN_SPOT
    }

    fun setMove(player: Char, location: Int): Boolean {
        if (location in 0 until BOARD_SIZE && board[location] == OPEN_SPOT) {
            board[location] = player
            return true
        }
        return false
    }

    fun getBoardOccupant(location: Int): Char {
        return if (location in 0 until BOARD_SIZE) board[location] else OPEN_SPOT
    }

    // Setter para la dificultad
    fun setDifficultyLevel(level: DifficultyLevel) {
        difficultyLevel = level
    }

    fun getDifficultyLevel(): DifficultyLevel {
        return difficultyLevel
    }

    /**
     * Método principal para obtener el movimiento de la computadora
     * Usa la dificultad seleccionada para determinar la estrategia
     */
    fun getComputerMove(): Int {
        return when (difficultyLevel) {
            DifficultyLevel.EASY -> {
                // 50% probabilidad de jugar al azar, 50% de jugar estratégicamente
                if (Random.nextBoolean()) {
                    getRandomMove()
                } else {
                    getStrategicMove()
                }
            }
            DifficultyLevel.HARDER -> {
                // Estrategia original: ganar, bloquear, al azar
                getStrategicMove()
            }
            DifficultyLevel.EXPERT -> {
                // Juego perfecto: ganar, bloquear, centro, esquina, lado
                getExpertMove()
            }
        }
    }

    /**
     * Obtiene un movimiento completamente al azar
     */
    private fun getRandomMove(): Int {
        val availableMoves = board.indices.filter { board[it] == OPEN_SPOT }
        return if (availableMoves.isNotEmpty()) {
            availableMoves.random(Random)
        } else 0
    }

    /**
     * Busca un movimiento ganador para la computadora
     */
    private fun getWinningMove(): Int {
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                board[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    board[i] = OPEN_SPOT
                    return i
                }
                board[i] = OPEN_SPOT
            }
        }
        return -1 // No hay movimiento ganador
    }

    /**
     * Busca un movimiento para bloquear al jugador humano
     */
    private fun getBlockingMove(): Int {
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                board[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    board[i] = OPEN_SPOT
                    return i
                }
                board[i] = OPEN_SPOT
            }
        }
        return -1 // No hay movimiento para bloquear
    }

    /**
     * Estrategia original: ganar, bloquear, al azar
     */
    private fun getStrategicMove(): Int {
        // Primero trata de ganar
        val winningMove = getWinningMove()
        if (winningMove != -1) return winningMove

        // Luego trata de bloquear al oponente
        val blockingMove = getBlockingMove()
        if (blockingMove != -1) return blockingMove

        // Si no puede ganar ni bloquear, juega al azar
        return getRandomMove()
    }

    /**
     * Estrategia experta: ganar, bloquear, centro, esquinas, lados
     */
    private fun getExpertMove(): Int {
        // Primero trata de ganar
        val winningMove = getWinningMove()
        if (winningMove != -1) return winningMove

        // Luego trata de bloquear al oponente
        val blockingMove = getBlockingMove()
        if (blockingMove != -1) return blockingMove

        // Toma el centro si está disponible
        if (board[4] == OPEN_SPOT) return 4

        // Toma una esquina si está disponible
        val corners = intArrayOf(0, 2, 6, 8)
        for (corner in corners) {
            if (board[corner] == OPEN_SPOT) return corner
        }

        // Toma un lado si está disponible
        val sides = intArrayOf(1, 3, 5, 7)
        for (side in sides) {
            if (board[side] == OPEN_SPOT) return side
        }

        // Fallback: movimiento al azar
        return getRandomMove()
    }

    fun checkForWinner(): Int {
        val lines = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8), // filas
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8), // columnas
            intArrayOf(0,4,8), intArrayOf(2,4,6)                      // diagonales
        )

        for (line in lines) {
            if (board[line[0]] != OPEN_SPOT &&
                board[line[0]] == board[line[1]] &&
                board[line[1]] == board[line[2]]) {
                return if (board[line[0]] == HUMAN_PLAYER) 2 else 3
            }
        }
        return if (board.all { it != OPEN_SPOT }) 1 else 0
    }

    fun isHumanFirst(): Boolean = humanFirst

    fun switchFirstPlayer() {
        humanFirst = !humanFirst
    }
}
